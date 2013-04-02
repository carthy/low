(ns low.native.utils
  (:import (java.nio IntBuffer LongBuffer)
           (com.sun.jna Pointer NativeLong NativeLibrary Function)))

(def native-long
  (case NativeLong/SIZE
    4 {:class Integer/TYPE
       :cast int
       :buffer IntBuffer
       :value #(.intValue %)}
    8 {:class Long/TYPE
       :cast long
       :buffer LongBuffer
       :value #(.longValue %)}))

(defn parse-unsigned-number [x]
  (->> x Long/toHexString (str "0x") read-string))

(defn buff-to-vec [buf]
  (vec (.array buf)))

(def unsigned-buff-to-vec
  (comp (partial mapv parse-unsigned-number) buff-to-vec))

(defmacro seq-to-buff [buf type]
  `(fn [seq#]
     (if (seq? seq#)
       (. ~buf ~'wrap (into-array ~type seq#))
       seq#)))

(defn arr [coll]
  (if (coll? coll)
    (into-array Pointer coll)
    coll))

(defn map-parr [fn coll]
  (into-array Pointer (map fn coll)))

(defn invoke [f ret-class args]
  (if (= Void/TYPE ret-class)
    (.invoke ^Function f (to-array args))
    (.invoke ^Function f ret-class (to-array args))))

(defn load-lib [lib]
  (NativeLibrary/getInstance lib))

(defn get-function [lib name]
  (if (string? lib)
    (Function/getFunction ^String lib ^String name)
    (.getFunction ^NativeLibrary lib name)))
