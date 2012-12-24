(ns low.api.value.use
  (:refer-clojure :exclude [first next])
  (:require [low.llvm :refer [LLVM]]))

(defn first [value]
  (LLVM :GetFirstUse value))

(defn next [use]
  (LLVM :GetNextUse use))

(defn uses [value]
  (let [first-use (first value)]
    (lazy-seq (cons first-use
                    (take-while deref ;; while not nil
                                (iterate next value))))))

(defn user [use]
  (LLVM :GetUser use))

(defn used-value [use]
  (LLVM :GetUsedValue use))
