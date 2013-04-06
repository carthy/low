(ns low.api.module.function
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add [module name function]
  "Adds the given function in the module"
  (LLVM :AddFunction module name function))

(defn get [module name]
  "Gets the function from the module"
  (LLVM :GetNamedFunction module name))

(defn first
  "Returns the first function in the module"
  [module]
  (LLVM :GetFirstFunction module))

(defn last
  "Returns the last function in the module"
  [module]
  (LLVM :GetLastFunction module))

(defn next
  "Returns the next function"
  [function]
  (LLVM :GetNextFunction function))

(defn prev
  "Returns the previous function"
  [function]
  (LLVM :GetPreviousFunction function))
