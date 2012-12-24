(ns low.api.value.function.argument
  (:refer-clojure :exclude [count get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn count [function]
  (LLVM :CountParams function))

(defn get [function idx]
  (LLVM :GetParam function idx))

(defn first [function]
  (LLVM :GetFirstParam function))

(defn last [function]
  (LLVM :GetLastParam function))

(defn next [function]
  (LLVM :GetNextParam function))

(defn prev [function]
  (LLVM :GetPreviousParam function))

;; could use GetParams
(defn params [function]
  (lazy-seq (cons (first function)
                  (take-while deref
                              (repeatedly #(next function))))))

(defn alignment! [param alignment]
  (LLVM :SetParamAlignment param alignment))

(defn function [param]
  (LLVM :GetParamParent param))

;; attrs
(defn get-attr [param]
  (LLVM :GetAttribute param))

(defn add-attr [param attr]
  (LLVM :AddAttribute param attr))

(defn del-attr [param attr]
  (LLVM :RemoveAttribute param attr))
