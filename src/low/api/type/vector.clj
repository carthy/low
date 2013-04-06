(ns low.api.type.vector
  (:require [low.llvm :refer [LLVM]]))

(defn size
  "Returns the size of the vector type"
  [vector]
  (LLVM :GetVectorSize vector))
