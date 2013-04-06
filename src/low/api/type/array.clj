(ns low.api.type.array
  (:require [low.llvm :refer [LLVM]]))

(defn size
  "Returns the size of the array type"
  [array]
  (LLVM :GetArrayLength array))
