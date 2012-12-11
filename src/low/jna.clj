(ns low.jna
  (:require [low.utils :refer [get-keys get-values]])
  (:import
   (clojure.lang IDeref IRecord)
   (com.sun.jna Function Native NativeLibrary Pointer)
   (java.nio ByteBuffer ByteOrder)))

(defmacro def-enum [name coll]
  `(let [coll# ~coll
         r-coll# (zipmap (get-values coll#) (get-keys coll#))]
     (def ~name
       {:name '~name
        :type ~Integer
        :fn (fn [key#]
              (if (number? key#)
                (coll# key#)
                (r-coll# key#)))})))

(defmacro def-type-alias [name type]
  `(def ~name {:name '~name :type ~type}))

(defmacro def-pointers [& aliases]
  (cons 'do
        (for [alias aliases]
          `(def-type-alias ~alias Pointer))))

(defn get-type [t]
  (if (map? t)
    (get-type (:type t))
    (if (class? t)
      t
      (class t))))

(defn get-type* [t]
  (let [t (get-type t)]
    (if (or (= Integer t)
            (= Long t)
            (= Boolean t))
      Number
      t)))

(defn virtual-type [t]
  (if (map? t)
    (:name (:type t))
    (if (class? t)
      t
      (class t))))

(defn matching-types [args r]
  (every? true? (map = (map get-type* args)
                     (map get-type* r))))

(defrecord ReturnExpr [ret type]
  IDeref
  (deref [this] ret))
(prefer-method print-method IDeref IRecord)

(defn adjust [args r]
  (let [ret (map #(if (instance? ReturnExpr %) @% %) r)]
    (map #((or (when (map? %) (:fn %))
               identity)
           %2) args ret)))

(defn load-lib [lib]
  (NativeLibrary/getInstance lib))

(defn get-function [lib name]
  (if (string? lib)
    (Function/getFunction lib name)
    (.getFunction ^NativeLibrary lib name)))

(defn import-function [lib name args ret-type]
  (let [f (get-function lib name)
        ret-class (get-type ret-type)
        fun (when (map? ret-type)
              (:fn ret-type))]
    (fn [& r]
      (assert (matching-types args r))
      (map->ReturnExpr
       {:ret ((or fun identity)
              (.invoke f ret-class (to-array (adjust args r))))
        :type ret-type}))))

(defn array-type [type seq]
  (map->ReturnExpr {:ret (into-array (map deref seq))
                    :type type}))

(defn malloc [size]
  (let [buffer (doto (ByteBuffer/allocateDirect size)
                 (.order ByteOrder/LITTLE_ENDIAN))
        pointer (Native/getDirectBufferPointer buffer)]
    {:pointer pointer :buffer buffer}))
