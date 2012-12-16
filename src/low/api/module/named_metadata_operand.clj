(ns low.api.module.named-metadata-operand
  (:refer-clojure :exclude [count])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn add [module name val]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :AddNamedMetadataOperand module name val))

(defn count [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :GetNamedMetadataNumOperands module name))

(defn operands [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (let [count @(count module name)
        ret (pointer :value count)]
    (LLVM :GetNamedMetadataOperands module name ret)
    (to-ptr-vec ret count)))
