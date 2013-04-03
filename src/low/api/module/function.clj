(ns low.api.module.function
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add [module name function]
  "Add the given function in the module"
  (LLVM :AddFunction module name function))

(defn get [module name]
  "Get the function from the module"
  (LLVM :GetNamedFunction module name))

(defn first [module]
  "Return the first function in the module"
  (LLVM :GetFirstFunction module))

(defn last [module]
  "Return the last function in the module"
  (LLVM :GetLastFunction module))

(defn next [function]
  "Return the next function"
  (LLVM :GetNextFunction function))

(defn prev [function]
  "Return the previous function"
  (LLVM :GetPreviousFunction function))
