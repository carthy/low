(ns low.api.value.variable
  (:require [low.llvm :refer [LLVM]]))

(defn destroy! [global]
  (LLVM :DeleteGlobal global))

(defn initializer [global]
  (LLVM :GetInitialier global))

(defn initializer! [global val]
  (LLVM :SetInitialier global val))

(defn thread-local? [global]
  (LLVM :IsThreadLocal global))

(defn thread-local! [global thread-local?]
  (LLVM :SetThreadLocal global thread-local?))

(defn constant? [global]
  (LLVM :IsGlobalConstant global))

(defn constant! [global constant?]
  (LLVM :SetGlobalConstant global constant?))
