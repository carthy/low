(ns low.llvm
  (:require [low.jna :refer :all :rename {def-enum def-enum*}])
  (:import (com.sun.jna Pointer)))

(def ^:private llvm-function-map (atom {}))
(def ^:private llvm-lib (promise))
(def ^:private llvm-version (promise))

(defn import-llvm-function [f-name args ret-type]
  (swap! llvm-function-map assoc f-name
         (import-function @llvm-lib (str "LLVM" (name f-name)) args ret-type)))

(defmacro def-enum [n & args]
  (let [enum-versioned (partition 2 args)
        enum-map (reduce #(reduce (fn [ret k] (assoc ret k (second %2)))
                                  % (first %2))
                         {} enum-versioned)]
    `(def-enum* ~n
       ~(or (enum-map @llvm-version) {}))))

(declare llvm-api)

(defn setup-llvm [ver]
  (deliver llvm-version ver)
  (deliver llvm-lib (load-lib (str "LLVM-" ver)))
  (load "llvm/api")
  (doseq [[f-name args ret-type versions] llvm-api]
    (when (versions ver)
        (import-llvm-function f-name args ret-type))))

(defn LLVM [f & args]
  (if-let [f (@llvm-function-map f)]
    (apply f args)
    (throw (ex-info "Function not found" {:fn-name (str "LLVM" f)}))))
