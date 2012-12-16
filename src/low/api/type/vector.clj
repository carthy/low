(ns low.api.type.vector
  (:require [low.llvm :refer [LLVM]]))

(defn size [vector]
  (LLVM :GetVectorSize vector))
