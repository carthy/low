(ns low.api.module.function
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add [module name function]
  (LLVM :AddFunction module name function))

(defn get [module name]
  (LLVM :GetNamedFunction module name))

(defn first [module]
  (LLVM :GetFirstFunction module))

(defn last [module]
  (LLVM :GetLastFunction module))

(defn next [module]
  (LLVM :GetNextFunction module))

(defn prev [module]
  (LLVM :GetPreviousFunction module))

(defn functions [module]
  (lazy-seq (cons (first module)
                  (take-while deref
                              (repeatedly #(next module))))))
