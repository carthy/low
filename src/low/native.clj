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
    (Function/getFunction lib name)
    (.getFunction ^NativeLibrary lib name)))

(def native-long
  (case NativeLong/SIZE
    4 [Integer int]
    8 [Long long]))

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

(defmacro defpointers [& types]
  (cons 'do
        (for [type types]
          `(typedef ~type Pointer))))

(defn type? [t]
  (instance? Type t))

(defn expr? [t]
  (instance? Expr t))

(when (empty? @type-map)
  (reset! type-map
          {:char (->Type :char Byte/TYPE identity byte)
           :wchar_t (->Type :wchar_t Character/TYPE identity char)
           :byte (->Type :byte Byte/TYPE identity byte)
           :short (->Type :short Short/TYPE identity short)
           :int (->Type :int Integer/TYPE identity int)
           :unsigned (->Type :unsigned (first native-long) identity (second native-long))
           ;; :bool (->Type :bool Boolean/TYPE identity #(if (true? %) 1 0))
           :bool (->Type :bool Boolean/TYPE identity boolean)
           :size_t (->Type :size_t (first native-long) identity (second native-long))
           :long (->Type :long (first native-long) identity (second native-long))
           :longlong (->Type :longlong Long/TYPE identity long)
           :__int64 (->Type :__int64 Long/TYPE identity long)
           :i8 (->Type :i8 Byte/TYPE identity byte)
           :i16 (->Type :i16 Short/TYPE identity short)
           :i32 (->Type :i32 Integer/TYPE identity int)
           :i64 (->Type :i64 Long/TYPE identity long)
           :float (->Type :float Float/TYPE identity float)
           :double (->Type :double Double/TYPE identity double)
           :void (->Type :void Void/TYPE identity identity)
           :void* (->Type :void* Pointer identity identity)
           :byte* (->Type :byte* ByteBuffer identity identity)
           ;;:char* (->Type :char* ByteBuffer identity identity)
           :char* (->Type :char* String identity identity)
           :constchar* (->Type :constchar* String identity identity)
           :wchar_t* (->Type :wchar_t* CharBuffer identity identity)
           :constwchar_t* (->Type :constwchar_t* WString identity identity)
           :short* (->Type :short* ShortBuffer identity identity)
           :int* (->Type :int* IntBuffer identity identity)
           :long* (->Type :long* native-long-buffer identity identity)
           :size_t* (->Type :size_t* native-long-buffer identity identity)
           :longlong* (->Type :longlong* LongBuffer identity identity)
           :__int64* (->Type :__int64* LongBuffer identity identity)
           :i8* (->Type :i8* ByteBuffer identity identity)
           :i16* (->Type :i16* ShortBuffer identity identity)
           :i32* (->Type :i32* IntBuffer identity identity)
           :i64* (->Type :i64* LongBuffer identity identity)
           :float* (->Type :float* FloatBuffer identity identity)
           :double* (->Type :double* DoubleBuffer identity identity)}))

(defn get-type [t]
  (let [t (if (expr? t) (:type t) t)
        t (:type (@type-map t))]
    (if (keyword? t)
      (get-type t)
      t)))

(defn get-type* [t]
  (let [t (get-type t)]
    (if (#{Byte/TYPE Boolean/TYPE Character/TYPE Short/TYPE
           Integer/TYPE Long/TYPE Float/TYPE Double/TYPE
           Byte Boolean Character Short Integer Long Float Double} t)
      Number
      (if (#{Memory Pointer} t)
        Pointer
        t))))

(defn adjust [types args]
  (map #(if (expr? %2)
          %2
          (if (= (get-type* %)
                 (get-type* %2))
            (->Expr %2 %)))
       types args))

(defn matching-types [args r]
  (and (= (count args)
          (count r))
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
                  (.invoke f (to-array (map bind r)))
                  (.invoke f ret-class (to-array (map bind r)))))
          :type ret-type})))))

;; (defn malloc [size]
;;   (let [buffer (doto (ByteBuffer/allocateDirect size)
;;                  (.order ByteOrder/LITTLE_ENDIAN))
;;         pointer (Native/getDirectBufferPointer buffer)]
;;     {:pointer pointer :buffer buffer}))

(defn pointer [type]
  (->Expr (doto (Memory. Pointer/SIZE) .clear)
          (keyword (str (name type) "*"))))

(defn & [ptr]
  (let [t-name (name (:type ptr))]
   (->Expr (.getPointer @ptr 0)
           (keyword (.substring t-name 0 (dec (.length t-name)))))))

(defn to-str [ptr]
  (.getString @ptr 0))

(defn to-ptr-vec [ptr cnt]
  (vec (.getPointerArray @ptr 0 cnt)))

(defn array-of [type seq]
  (map->Expr {:val (into-array (map bind seq))
              :type (keyword (str (name type) "*"))}))
