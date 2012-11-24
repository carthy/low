(ns low.jna
  (:import (com.sun.jna Function NativeLibrary)))

(defn load-lib [lib]
  (NativeLibrary/getInstance lib))

(defn get-function [lib name]
  (if (string? lib)
    (Function/getFunction lib name)
    (.getFunction ^NativeLibrary lib name)))

(defn import-function [lib name ret-type args-len]
  (let [f (get-function lib name)]
    (fn [& args]
      (assert (== args-len (count args)))
      (.invoke f ret-type (to-array args)))))
