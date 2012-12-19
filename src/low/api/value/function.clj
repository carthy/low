(ns low.api.value.function
  (:require [low.llvm :refer [LLVM]]))

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

;; attrs
(defn get-addr [function]
  (LLVM :GetFunctionAttr function))

(defn add-attr [function attr]
  (LLVM :AddFunctionAttr function addr))

(defn del-attr [function attr]
  (LLVM :RemoveFunctionAttr function attr))
