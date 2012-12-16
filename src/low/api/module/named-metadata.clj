(ns low.api.module.named-metadata
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn operand! [module name val]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :AddNamedMetadataOperand module name val))

(defn operands-count [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :GetNamedMetadataNumOperands module name))

(defn operands [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (let [count @(operands-count module name)
        ret (pointer :value count)]
    (LLVM :GetNamedMetadataOperands module name ret)
    (to-ptr-vec ret count)))
