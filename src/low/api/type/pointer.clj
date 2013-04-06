(ns low.api.type.pointer
  (:require [low.llvm :refer [LLVM]]))

(defn address-space
  "Returns the adress space of the pointer type"
  [pointer]
  (LLVM :GetPointerAddressSpace pointer))
