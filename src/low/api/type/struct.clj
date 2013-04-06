(ns low.api.type.struct
  (:refer-clojure :exclude [name])
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn body!
  "Sets the body of the struct type"
  [struct element-types packed?]
  (LLVM :StructSetBody struct
         element-types
        (count element-types) packed?))

(defn name [t]
  "Returns the name of the struct type"
  (LLVM :GetStructName t))

(defn packed?
  "Returns true if the struct type is packed"
  [struct]
  (LLVM :IsPackedStruct struct))

(defn opaque?
  "Returns true if the struct type is opaque"
  [struct]
  (LLVM :IsOpaqueStruct struct))

(defn element-count
  "Returns the number of elements for the struct type"
  [struct]
  (LLVM :CountStructElementTypes struct))

(defn element-types
  "Returns a vector containing the argument types of the struct type"
  [struct]
  (let [element-c @(element-count struct)
        ret (pointer :type element-c)]
    (LLVM :GetStructElementTypes struct ret)
    (to-ptr-vec ret element-c)))
