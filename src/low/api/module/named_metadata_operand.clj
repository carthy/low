(ns low.api.module.named-metadata-operand
  (:refer-clojure :exclude [count])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn add [module name val]
  "Add the named metadata operand to the module"
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :AddNamedMetadataOperand module name val))

(defn count [module name]
  "Return the number of named metadata operands of the module"
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :GetNamedMetadataNumOperands module name))
