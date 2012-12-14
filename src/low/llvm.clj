(ns low.llvm
  (:refer-clojure :exclude [type])
  (:require [low.native :refer :all :rename {defenum defenum*}]))

(defonce ^:private llvm-function-map (atom {}))
(defonce ^:private llvm-lib (promise))
(defonce llvm-version (promise))

(defn import-llvm-function [f-name args ret-type]
  (swap! llvm-function-map assoc f-name
         (import-function @llvm-lib (str "LLVM" (name f-name)) args ret-type)))

(defmacro defenum [n & args]
  (if (set? (first args))
    (let [enum-versioned (partition 2 args)
          enum-map (reduce #(reduce (fn [ret k] (assoc ret k (second %2)))
                                    % (first %2))
                           {} enum-versioned)]
      `(defenum* ~n
         ~(or (enum-map @llvm-version) {})))
    `(defenum* ~n
       ~@args)))

(declare llvm-api)
(def ^:private loader (future (load "llvm/api")))

(defn setup-llvm [ver]
  (deliver llvm-version ver)
  (deliver llvm-lib (load-lib (str "LLVM-" ver)))
  @loader
  (doseq [[f-name args ret-type versions] llvm-api]
    (when ((or versions #{ver}) ver)
      (import-llvm-function f-name args ret-type))))

(defn LLVM [f & args]
  (if-let [f (@llvm-function-map f)]
    (apply f args)
    (throw (ex-info "Function not found" {:fn-name (str "LLVM" f)}))))
