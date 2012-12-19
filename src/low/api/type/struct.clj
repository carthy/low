(ns low.api.type.struct
  (:refer-clojure :exclude [name])
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn body! [struct element-types packed?]
  (LLVM :StructSetBody struct
         element-types
        (count element-types) packed?))

(defn name [t]
  (LLVM :GetStructName t))

(defn packed? [struct]
  (LLVM :IsPackedStruct struct))

(defn opaque? [struct]
  (LLVM :IsOpaqueStruct struct))

(defn element-count [struct]
  (LLVM :CountStructElementTypes struct))

(defn element-types [struct]
  (let [element-c @(element-count struct)
        ret (pointer :type element-c)]
    (LLVM :GetStructElementTypes struct ret)
    (to-ptr-vec ret element-c)))
