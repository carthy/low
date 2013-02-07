(ns low.api.value.function
  (:require [low.llvm :refer [LLVM]]
            [low.api.value.function.argument :as arg]
            [low.api.value.function.basic-block :as fbb]
            [low.api.value.basic-block :as bb]))

(defn delete! [function]
  (LLVM :DeleteFunction function))

(defn id [function]
  (LLVM :GetIntrinsicID function))

(defn call-conv [function]
  (LLVM :GetFunctionCallConv function))

(defn call-conv! [function call-conv]
  (LLVM :SetFunctionCallConv function call-conv))

(defn GC [function]
  (LLVM :GetGC function))

(defn GC! [function GC]
  (LLVM :SetGC function GC))

;; could use GetParams
(defn arguments [function]
  (let [first-arg (arg/first function)]
    (lazy-seq (cons first-arg
                    (take-while deref
                                (iterate arg/next first-arg))))))

;; could use GetBasicBlocks
(defn basic-blocks [function]
  (let [first-bb (fbb/first function)]
    (lazy-seq (cons first-bb
                    (take-while deref
                                (iterate bb/next first-bb))))))

;; attrs
(defn get-attr [function]
  (LLVM :GetFunctionAttr function))

(defn add-attr [function attr]
  (LLVM :AddFunctionAttr function attr))

(defn del-attr [function attr]
  (LLVM :RemoveFunctionAttr function attr))
