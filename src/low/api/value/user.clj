(ns low.api.value.user
  (:require [low.llvm :refer [LLVM]]))

(defn operand [user index]
  (LLVM :GetOperand user index))

(defn operand! [user index val]
  (LLVM :SetOperand user index val))

(defn operands-count [user]
  (LLVM :GetNumOperand user))

(defn operands [user]
  (for [i (range (operands-count user))]
    (operand user i)))
