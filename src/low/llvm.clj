(ns low.llvm
  (:require [low.jna :refer [import-function load-lib]])
  (:import (com.sun.jna Pointer)))

(load "llvm_api")

(def ^:private llvm-function-map (atom {}))
(def ^:private llvm-lib (promise))

(defn import-llvm-function [f-name ret-type args-len]
  (let [[ret-type wrapping-fun]
        (if (class? ret-type)
          [ret-type identity]
          [Integer (fn [f] (fn [& args] (ret-type (apply f args))))])]
    (swap! llvm-function-map assoc f-name
           (wrapping-fun
            (import-function @llvm-lib (str "LLVM" (name f-name)) ret-type args-len)))))

(defn setup-llvm [ver]
  (deliver llvm-lib (load-lib (str "LLVM-" ver)))
  (doseq [[f-name ret-type args-len] llvm-api]
    (import-llvm-function f-name ret-type args-len)))

(defn LLVM [f & args]
  (if-let [f (@llvm-function-map f)]
    (apply f args)
    (throw (ex-info "Function not found" {:fn-name (str "LLVM" f)}))))
