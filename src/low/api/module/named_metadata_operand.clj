(ns low.api.module.named-metadata-operand
  (:refer-clojure :exclude [count])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn add
  "Add the named metadata operand to the module"
  [module name val]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :AddNamedMetadataOperand module name val))

(defn count
  "Return the number of named metadata operands of the module"
  [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (LLVM :GetNamedMetadataNumOperands module name))
