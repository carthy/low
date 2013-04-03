(ns low.api.module.function
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add [module name function]
  "Add the given function in the module"
  (LLVM :AddFunction module name function))

(defn get [module name]
  "Get the function from the module"
  (LLVM :GetNamedFunction module name))

(defn first
  "Return the first function in the module"
  [module]
  (LLVM :GetFirstFunction module))

(defn last
  "Return the last function in the module"
  [module]
  (LLVM :GetLastFunction module))

(defn next
  "Return the next function"
  [function]
  (LLVM :GetNextFunction function))

(defn prev
  "Return the previous function"
  [function]
  (LLVM :GetPreviousFunction function))
