(ns low.native
  (:require [low.utils :refer :all])
  (:import (clojure.lang IDeref)
           (java.io Writer)
           (java.nio ByteBuffer IntBuffer CharBuffer ByteOrder Buffer
                     ShortBuffer LongBuffer FloatBuffer DoubleBuffer)
           (com.sun.jna NativeLong Pointer WString NativeLibrary
                        Memory Function)))

(declare expr? ret-f bind-f make-expr register-type)

;;; core abstractions ;;;
(defonce type-map (atom {}))

(defrecord Type [name type ret-f bind-f])

(defrecord Expr [val type #_err #_out]
  IDeref
  (deref [this]
    ((ret-f type) val)))

;;; print-methods ;;;
(defmethod print-method Type [^Type t ^Writer writer]
  (.write writer (str "<Type: " (:name t) " -> " (:type t) ">")))

(defmethod print-method Expr [^Expr t ^Writer writer]
  (.write writer (str "<Expr: " (or @t
                                    (if (false? @t)
                                      "false"
                                      "nil")) " (type: "))
  (print-method (@type-map (:type t)) writer)
  (.write writer ")>"))

;;; private helpers ;;;
(def ^:private native-long
  (case NativeLong/SIZE
    4 [Integer/TYPE int]
    8 [Long/TYPE long]))

(def ^:private native-long-buffer
  (case NativeLong/SIZE
    4 IntBuffer
    8 LongBuffer))

(defn ^:private bind-f [t]
  (or (:bind-f (@type-map t))
      identity))

(defn ^:private ret-f [t]
  (or (:ret-f (@type-map t))
      identity))

(defn ^:private parse-unsigned-number [x]
  (->> x Long/toHexString (str "0x") read-string))

(defn ^:private uchar-f [x]
  (if (neg? x)
    (.byteValue x)
    x))

(defn ^:private uint-f [x]
  (if (neg? x)
    (if (= long (second native-long)) (.longValue x) (.intValue x))
    x))

(defn ^:private ulonglong-f [x]
  (if (neg? x)
    (.longValue x)
    x))

(defn ^:private recast-arr [buf type]
  (fn [seq]
    (if (seq? seq)
      (eval `(. ~buf wrap (into-array ~type '~seq)))
      seq)))

(def ^:private buff-vec
  (fn [buf] (vec (.array buf))))

(def ^:private arr
  (fn [coll]
    (if (coll? coll)
      (to-array coll)
      coll)))

(defn ^:private adjust [types args]
  (map (fn [type val]
         (if (expr? val)
           val (make-expr val type)))
       types args))

(defn ^:private matching-types [args r]
  (and (= (count args) (count r))
       (every? true? (map = args (map :type r)))))

(def ^:private parse-unsigned-buff
  (comp (partial mapv parse-unsigned-number) buff-vec))

;;; def macros ;;;
(defmacro typedef [name type & [ret-f bind-f]]
  (let [name (keyword name)]
    `(register-type '~name ~type
                ~(or ret-f identity)
                ~(or bind-f identity))))

(defmacro defenum [name coll]
  `(let [coll# ~coll
         r-coll# (zipmap (get-values coll#) (get-keys coll#))
         bind-f# (fn [x#] (or (get coll# x#)
                              (get r-coll# x#)))
         return-f# #(coll# %)]
     (typedef ~name :int return-f# bind-f#)))

(defmacro defopaques [& types]
  (cons 'do
        (for [type types]
          `(typedef ~type Pointer identity identity))))

(defmacro defpointers [& types]
  (cons 'do
        (for [type types]
          `(typedef ~type Pointer identity ~arr))))

;;; helpers ;;;
(defn type? [t]
  (instance? Type t))

(defn expr? [t]
  (instance? Expr t))

(defn make-expr [val type]
  (->Expr (if (expr? val) val ((bind-f type) val)) type))

(defn register-type [name type ret-f bind-f]
  (swap! type-map assoc name
         (->Type name type ret-f bind-f)))

(defn internal-type [t]
  (let [t (if (expr? t) (:type t) t)]
    (if (keyword? t) ;; an alias
      (internal-type (:type (@type-map t)))
      (if (class? t)
        t
        (class t)))))

;;; api functions ;;;
(defn load-lib [lib]
  (NativeLibrary/getInstance lib))

(defn get-function [lib name]
  (if (string? lib)
    (Function/getFunction ^String lib ^String name)
    (.getFunction ^NativeLibrary lib name)))

(defn invoke-function [f ret-class args]
  (if (= Void/TYPE ret-class)
    (.invoke ^Function f (to-array args))
    (.invoke ^Function f ret-class (to-array args))))

(defn import-function [lib name args ret-type]
  (let [f (get-function lib name)
        ret-class (internal-type ret-type)]
    (fn [& r]
      (let [r (adjust args r)]
        (assert (matching-types args r))
        (let [ret (invoke-function f ret-class (mapv :val r))]
          (make-expr ret ret-type))))))

(defn pointer
  ([type] (pointer type 1))
  ([type n]
     (let [t (keyword (str (name type) "*"))
           type (t @type-map)]
       (if (and type ((supers (:type type)) Buffer))
         (make-expr (eval (list '. (:type type) 'allocate n)) t)
         (make-expr (doto (Memory. (* n Pointer/SIZE)) .clear) t)))))

(defn & [pointer]
  (let [t-name (name (:type pointer))]
    (make-expr (.getPointer ^Pointer @pointer 0)
               (keyword (.substring t-name 0 (dec (.length t-name)))))))

(defn to-str
  ([ptr]
     (.getString ^Pointer @ptr 0))
  ([ptr len]
     (.getString ^Pointer @ptr 0 len)))

(defn to-ptr-vec [ptr cnt]
  (vec (.getPointerArray ^Pointer @ptr 0 cnt)))

;;; initialize type map;;:
(when (empty? @type-map)
  (dorun
   (map (partial apply register-type)
        [[:void Void/TYPE identity identity]
         [:bool Boolean/TYPE identity boolean]
         [:char Byte/TYPE identity byte]
         [:wchar_t Character/TYPE identity char]
         [:short Short/TYPE identity short]
         [:int Integer/TYPE identity int]
         [:long (first native-long) identity (second native-long)]
         [:longlong Long/TYPE identity long]
         [:float Float/TYPE identity float]
         [:double Double/TYPE identity double]
         [:uchar Byte/TYPE parse-unsigned-number uchar-f]
         [:uint (first native-long) parse-unsigned-number uint-f]
         [:ulonglong Long/TYPE parse-unsigned-number ulonglong-f]
         ;;pointers
         [:void* Pointer identity identity]
         [:char* ByteBuffer buff-vec (recast-arr ByteBuffer Byte/TYPE)]
         [:wchar_t*  CharBuffer buff-vec (recast-arr CharBuffer Character/TYPE)]
         [:constchar* String identity identity]
         [:constwcahar_t WString str identity]
         [:short* ShortBuffer buff-vec (recast-arr ShortBuffer Short/TYPE)]
         [:int* IntBuffer buff-vec (recast-arr IntBuffer Integer/TYPE)]
         [:long* native-long-buffer buff-vec (recast-arr native-long-buffer (first native-long))]
         [:longlong* LongBuffer buff-vec (recast-arr LongBuffer Long/TYPE)]
         [:float* FloatBuffer buff-vec (recast-arr FloatBuffer Float/TYPE)]
         [:double* DoubleBuffer buff-vec (recast-arr DoubleBuffer Double/TYPE)]
         [:uchar* ByteBuffer parse-unsigned-buff (comp (recast-arr ByteBuffer Byte/TYPE)
                                                       (partial mapv uchar-f)
                                                       buff-vec)]
         [:uint* native-long-buffer parse-unsigned-buff (comp (recast-arr native-long-buffer (first native-long))
                                                              (partial mapv uint-f)
                                                              buff-vec)]
         [:ulonglong* LongBuffer parse-unsigned-buff (comp (recast-arr LongBuffer Long/TYPE)
                                                           (partial mapv ulonglong-f)
                                                           buff-vec)]]))

  (typedef byte :char)
  (typedef byte* :char*)
  (typedef size_t :long)
  (typedef size_t* :long*)
  (typedef uint8_t :uchar)
  (typedef uint64_t :ulonglong)
  (defpointers char**))
