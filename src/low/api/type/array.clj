(ns low.api.type.array
  (:require [low.llvm :refer [LLVM]]))

(defn size [array]
  (LLVM :GetArrayLength array))
