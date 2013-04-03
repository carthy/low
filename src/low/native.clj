(ns low.native
  (:refer-clojure :exclude [keys vals])
  (:require [low.native.utils :refer [native-long] :as u]
            [low.utils :refer :all :exclude [native-long]])
  (:import (clojure.lang IDeref)
           (java.io Writer)
           (java.nio ByteBuffer IntBuffer CharBuffer ByteOrder Buffer
                     ShortBuffer LongBuffer FloatBuffer DoubleBuffer)
           (com.sun.jna NativeLong Pointer WString NativeLibrary
                        Memory Function)))

(defonce type-map (atom {}))

(defprotocol Type
  (-bind [this v]))

(defprotocol Value
  (return [this]))

(defrecord NativeValue [internal-value type]
  Value
  (return [this]
    ((or (:ret-f (@type-map type))
         identity) internal-value))

  IDeref
  (deref [this] (return this)))

(defn native-value? [x]
  (instance? NativeValue x))

(defrecord NativeType [name type ret-f bind-f]
  Type
  (-bind [this v] (->NativeValue (bind-f v) name)))

(defn native-type? [x]
  (instance? NativeType x))

(defn register-type [name type ret-f bind-f]
  {:pre [(keyword? name)
         (or (class? type)
             (keyword? type))]}
  (swap! type-map assoc name
         (->NativeType name type ret-f bind-f)))

(defmacro typedef
  [name type & {:keys [ret bind]
                :or {ret identity
                     bind identity}}]
  {:pre [(symbol? name)]}
  (list `register-type (keyword name) type ret bind))

(typedef void Void/TYPE)
(typedef bool Boolean/TYPE
         :bind boolean)
(typedef char Byte/TYPE
         :bind byte)
(typedef wchar Character/TYPE
         :bind char)
(typedef short Short/TYPE
         :bind short)
(typedef int Integer/TYPE
         :bind int)
(typedef long (:class native-long)
         :bind (:cast native-long))
(typedef longlong Long/TYPE
         :bind long)
(typedef float Float/TYPE
         :bind float)
(typedef double Double/TYPE
         :bind double)
(typedef uchar Byte/TYPE
         :ret u/parse-unsigned-number
         :bind (pos-or (fn [x] (.byteValue x))))
(typedef uint (:class native-long)
         :ret u/parse-unsigned-number
         :bind (pos-or (:value native-long)))
(typedef ulonglong Long/TYPE
         :ret u/parse-unsigned-number
         :bind (pos-or (fn [x] (.longValue x))))

(typedef void* Pointer)
(typedef char* ByteBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff ByteBuffer Byte/TYPE))
(typedef wchar*  CharBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff CharBuffer Character/TYPE))
(typedef const_char* String)
(typedef const_wchar* WString
         :ret str)
(typedef short* ShortBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff ShortBuffer Short/TYPE))
(typedef int* IntBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff IntBuffer Integer/TYPE))
(typedef long* (:buffer native-long)
         :ret u/buff-to-vec
         :bind (u/seq-to-buff (:buffer native-long) (:class native-long)))
(typedef longlong* LongBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff LongBuffer Long/TYPE))
(typedef float* FloatBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff FloatBuffer Float/TYPE))
(typedef double* DoubleBuffer
         :ret u/buff-to-vec
         :bind (u/seq-to-buff DoubleBuffer Double/TYPE))
(typedef uchar* ByteBuffer
         :ret u/unsigned-buff-to-vec
         :bind (comp (u/seq-to-buff ByteBuffer Byte/TYPE)
                     (partial mapv (pos-or (fn [x] (.byteValue x))))))
(typedef uint* (:buffer native-long)
         :ret u/unsigned-buff-to-vec
         :bind (comp (u/seq-to-buff (:buffer native-long) (:class native-long))
                     (partial mapv (pos-or (:value native-long)))))
(typedef ulonglong* LongBuffer
         :ret u/unsigned-buff-to-vec
         :bind (comp (u/seq-to-buff LongBuffer Long/TYPE)
                     (partial mapv (pos-or (fn [x] (.longValue x))))))


(defmacro alias-typedef [alias type]
  {:pre [(every? symbol? [alias type])]}
  `(let [type# (@type-map ~(keyword type))]
     (typedef ~alias (:type type#)
              :ret (:ret-f type#)
              :bind (:bind-f type#))))

(alias-typedef byte char)
(alias-typedef byte* char*)
(alias-typedef size long)
(alias-typedef size* long*)
(alias-typedef uint8 uchar)
(alias-typedef uint64 ulonglong)

(defmacro defpointer [type]
  {:pre [(symbol? type)]}
  `(typedef ~type Pointer :bind u/arr))

(defpointer char**)

(defmacro defopaque [type]
  {:pre [(symbol? type)]}
  `(typedef ~type Pointer))

(defmacro defpointers [& types]
  (cons 'do (for [type types] `(defpointer ~type))))

(defmacro defopaques [& types]
  (cons 'do (for [type types] `(defopaque ~type))))

(defmacro defenum [name coll]
  `(let [coll# ~coll
         r-coll# (zipmap (vals coll#)
                         (keys coll#))
         bind-f# (fn [x#] (or (first (find coll# x#))
                             (get r-coll# x#)))
         return-f# #(get coll# %)]
     (typedef ~name Integer/TYPE :ret return-f# :bind bind-f#)))

(defn bind [type-name val]
  {:pre [(keyword? type-name)]}
  (if-let [type (@type-map type-name)]
    (-bind type val)
    val))

(defn value [v type]
  {:pre [(keyword? type)]}
  (bind type v))

(defn internal-type [type]
  {:pre [(keyword? type)]}
  (let [t (:type (@type-map type))]
    (if (keyword? t)
      (recur t)
      t)))

(defn ^:private adjust [types args]
  (map (fn [type val]
         (if (native-value? val)
           val
           (value val type)))
       types args))

(defn ^:private matching-types [args r]
  (= args (map :type r)))

(defn import-function [lib f-name args ret-type]
  {:pre [(keyword? ret-type)
         (keyword? f-name)
         (and (vector? args)
              (every? keyword? args))]}
  (let [name (name f-name)
        f (u/get-function lib name)
        ret-class (internal-type ret-type)]
    (fn [& r]
      {:pre (= (count args) (count r))}
      (let [r (adjust args r)]
        (assert (matching-types args r))
        (let [ret (u/invoke f ret-class (mapv :internal-value r))]
          (value ret ret-type))))))

(defn pointer
  ([type] (pointer type 1))
  ([type n]
     {:pre [(keyword? type)]}
     (let [t (keyword (str (name type) "*"))
           type (t @type-map)]
       (if (and type ((supers (:type type)) Buffer))
         (value (eval (list '. (:type type) 'allocate n)) t)
         (value (doto (Memory. (* n Pointer/SIZE)) .clear) t)))))

(defn & [pointer]
  {:pre [(native-type? pointer)]}
  (let [t-name (name (:type pointer))]
    (value (.getPointer ^Pointer @pointer 0)
           (keyword (subs t-name 0 (dec (count t-name)))))))

(defn to-str
  ([ptr]
     {:pre [(native-type? ptr)]}
     (.getString ^Pointer @ptr 0))
  ([ptr len]
     {:pre [(native-type? ptr)]}
     (.getString ^Pointer @ptr 0 len)))

(defn ptr-to-vec [pointer cnt]
  {:pre [(native-type? pointer)]}
  (vec (.getPointerArray ^Pointer @pointer 0 cnt)))
