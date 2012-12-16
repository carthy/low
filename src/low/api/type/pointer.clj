(ns low.api.type.pointer
  (:require [low.llvm :refer [LLVM]]))

(defn address-space [pointer]
  (LLVM :GetPointerAddressSpace pointer))
