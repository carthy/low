(ns low.api.value.function
  (:require [low.llvm :refer [LLVM]]
            [low.api.value.function.argument :as arg]))

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
  (lazy-seq (cons (arg/first function)
                  (take-while deref
                              (repeatedly #(arg/next function))))))

;; attrs
(defn get-attr [function]
  (LLVM :GetFunctionAttr function))

(defn add-attr [function attr]
  (LLVM :AddFunctionAttr function attr))

(defn del-attr [function attr]
  (LLVM :RemoveFunctionAttr function attr))
