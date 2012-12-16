(ns low.api.value.use
  (:refer-clojure :exclude [first next])
  (:require [low.llvm :refer [LLVM]]))

(defn first [value]
  (LLVM :GetFirstUse value))

(defn next [value]
  (LLVM :GetNextUse value))

(defn uses [value]
  (lazy-seq (cons (first value)
                  (take-while deref ;; while not nil
                              (repeatedly #(next value))))))

(defn user [use]
  (LLVM :GetUser use))

(defn used-value [use]
  (LLVM :GetUsedValue use))
