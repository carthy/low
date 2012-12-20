(ns low.native
  (:require [low.utils :refer :all])
  (:import (clojure.lang IDeref)
           (java.io Writer)
           (java.nio ByteBuffer IntBuffer CharBuffer ByteOrder
                     ShortBuffer LongBuffer FloatBuffer DoubleBuffer)
           (com.sun.jna NativeLong Pointer WString NativeLibrary
                        Memory Function)))

(defn load-lib [lib]
  (NativeLibrary/getInstance lib))

(defn get-function [lib name]
  (if (string? lib)
    (Function/getFunction ^String lib ^String name)
    (.getFunction ^NativeLibrary lib name)))

(def native-long
  (case NativeLong/SIZE
    4 [Integer/TYPE int]
    8 [Long/TYPE long]))

(def native-long-buffer
  (case NativeLong/SIZE
    4 IntBuffer
    8 LongBuffer))

(defonce type-map (atom {}))

(defrecord Type [name type ret-f bind-f])

(defmethod print-method Type [^Type t ^Writer writer]
  (.write writer (str "<Type: " (:name t) " -> " (:type t) ">")))

(defprotocol IBind
  (bind [val]))

(defrecord Expr [val type #_err #_out]
  IDeref
  (deref [this] val)
  IBind
  (bind [this]
    ((or (:bind-f (@type-map type))
         identity) val)))

(declare get-type)
(defmethod print-method Expr [^Expr t ^Writer writer]
  (.write writer (str "<Expr: " (or (:val t)
                                    (if (false? (:val t))
                                      "false"
                                      "nil")) " (type: "))
  (print (@type-map (:type t)))
  (.write writer ")>"))

(defmacro typedef [name type & [ret-f bind-f]]
  (let [name (keyword name)]
    `(swap! type-map assoc ~name
            (->Type '~name ~type
                    ~(or ret-f identity)
                    ~(or bind-f identity)))))

(defmacro defenum [name coll]
  `(let [coll# ~coll
         r-coll# (zipmap (get-values coll#) (get-keys coll#))
         bind-f# #(r-coll# %)
         return-f# #(coll# %)]
     (typedef ~name :int return-f# bind-f#)))

(declare ^:private arr)
(defmacro defpointers [& types]
  (cons 'do
        (for [type types]
          `(typedef ~type Pointer identity ~arr))))

(defn ^:private arr [coll]
  (if (coll? coll)
    (to-array (map bind coll))
    coll))

(defn type? [t]
  (instance? Type t))

(defn expr? [t]
  (instance? Expr t))

(defn ^:private parse-unsigned-number [x]
  (->> x Long/toHexString (str "0x") read-string))

(defn ^:private uchar-f [x]
  (.byteValue x))

(defn ^:private uint-f [x]
  (if (= long (second native-long)) (.longValue x) (.intValue x)))

(defn ^:private ulonglong-f [x]
  (.longValue x))

(when (empty? @type-map)
  (reset! type-map
          {:void (->Type :void Void/TYPE identity identity)
           :char (->Type :char Byte/TYPE identity byte)
           :wchar_t (->Type :wchar_t Character/TYPE identity char)
           :byte (->Type :byte Byte/TYPE identity byte)
           :short (->Type :short Short/TYPE identity short)
           :int (->Type :int Integer/TYPE identity int)
           :bool (->Type :bool Boolean/TYPE identity boolean)
           :long (->Type :long (first native-long) identity (second native-long))
           :longlong (->Type :longlong Long/TYPE identity long)
           :float (->Type :float Float/TYPE identity float)
           :double (->Type :double Double/TYPE identity double)
           :uchar (->Type :uchar Byte/TYPE parse-unsigned-number uchar-f)
           :uint (->Type :uint (first native-long) parse-unsigned-number uint-f)
           :ulonglong (->Type :ulonglong Long/TYPE parse-unsigned-number ulonglong-f)
           :void* (->Type :void* Pointer identity arr)
           :byte* (->Type :byte* ByteBuffer identity arr)
           :char* (->Type :char* ByteBuffer identity arr)
           :constchar* (->Type :constchar* String identity identity)
           :wchar_t* (->Type :wchar_t* CharBuffer identity arr)
           :constwchar_t* (->Type :constwchar_t* WString identity identity)
           :short* (->Type :short* ShortBuffer identity arr)
           :int* (->Type :int* IntBuffer identity arr)
           :long* (->Type :long* native-long-buffer identity arr)
           :size_t* (->Type :size_t* native-long-buffer identity arr)
           :longlong* (->Type :longlong* LongBuffer identity arr)
           :double* (->Type :double* DoubleBuffer identity arr)
           :uchar* (->Type :uchar* ByteBuffer (partial map parse-unsigned-number)
                           (comp to-array (partial map uchar-f)))
           :uint* (->Type :uint* native-long-buffer (partial map parse-unsigned-number)
                          (comp to-array (partial map uint-f)))
           :ulonglong* (->Type :ulonglong* LongBuffer (partial map parse-unsigned-number)
                               (comp to-array (partial map ulonglong-f)))})
  (typedef uint8_t :uchar)
  (typedef uint64_t :ulonglong)
  (typedef uint64_t* :ulonglong*)
  (defpointers char**))

(defn get-type [t]
  (let [t (if (expr? t) (:type t) t)]
    (if (keyword? t)
      (get-type (:type (@type-map t)))
      (if (class? t)
        t
        (class t)))))

(defn adjust [types args]
  (map #(if (expr? %2)
          %2
          (->Expr %2 %))
       types args))

(defn matching-types [args r]
  (and (= (count args) (count r))
       (every? true? (map = args (map :type r)))))

(defn import-function [lib name args ret-type]
  (let [f (get-function lib name)
        ret-class (get-type ret-type)]
    (fn [& r]
      (let [r (adjust args r)]
        (assert (matching-types args r))
        (map->Expr
         {:val ((:ret-f (@type-map ret-type))
                (if (= Void/TYPE ret-class)
                  (.invoke ^Function f (to-array (map bind r)))
                  (.invoke ^Function f ret-class (to-array (map bind r)))))
          :type ret-type})))))

(defn pointer
  ([type] (pointer type 1))
  ([type n]
     (->Expr (doto (Memory. (* n Pointer/SIZE)) .clear)
             (keyword (str (name type) "*")))))

(defn & [ptr]
  (let [t-name (name (:type ptr))]
    (->Expr (.getPointer ^Pointer @ptr 0)
            (keyword (.substring t-name 0 (dec (.length t-name)))))))

(defn to-str [ptr]
  (.getString ^Pointer @ptr 0))

(defn to-ptr-vec [ptr cnt]
  (vec (.getPointerArray ^Pointer  @ptr 0 cnt)))
