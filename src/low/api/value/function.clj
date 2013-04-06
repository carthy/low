(ns low.api.value.function
  (:require [low.llvm :refer [LLVM]]
            [low.api.value.function.argument :as arg]
            [low.api.value.function.basic-block :as bb]))

(defn delete!
  "Deletes the function"
  [function]
  (LLVM :DeleteFunction function))

(defn id
  "Returns the intrinsic ID of the function"
  [function]
  (LLVM :GetIntrinsicID function))

(defn call-conv
  "Returns the call conv of the function"
  [function]
  (LLVM :GetFunctionCallConv function))

(defn call-conv!
  "Sets the call conv of the function, see the call-conv enum"
  [function call-conv]
  (LLVM :SetFunctionCallConv function call-conv))

(defn GC
  "Returns the GC associated with the function"
  [function]
  (LLVM :GetGC function))

(defn GC!
  "Sets the GC for the function"
  [function GC]
  (LLVM :SetGC function GC))

;; could use GetParams but would not be lazy
(defn arguments
  "Returns a lazy seq of the arguments of the function"
  [function]
  (let [first-arg (arg/first function)]
    (lazy-seq (cons first-arg
                    (take-while deref
                                (iterate arg/next first-arg))))))

;; could use GetBasicBlocks but would not be lazy
(defn basic-blocks
  "Returns a lazy seq of the basic blocks of the function"
  [function]
  (let [first-bb (bb/first function)]
    (lazy-seq (cons first-bb
                    (take-while deref
                                (iterate bb/next first-bb))))))

;; attr
(defn get-attr
  "Get the function attribute"
  [function]
  (LLVM :GetFunctionAttr function))

(defn add-attr
  "Add an attribute to the function"
  [function attr]
  (LLVM :AddFunctionAttr function attr))

(defn delete!-attr
  "Deletes the function attribute"
  [function attr]
  (LLVM :RemoveFunctionAttr function attr))
