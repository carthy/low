(ns low.api.value.user-operand
  (:refer-clojure :exclude [get set! count])
  (:require [low.llvm :refer [LLVM]]))

(defn get [user index]
  (LLVM :GetOperand user index))

(defn set! [user index val]
  (LLVM :SetOperand user index val))

(defn count [user]
  (LLVM :GetNumOperand user))

(defn operands [user]
  (for [i (range @(count user))]
    (get user i)))
