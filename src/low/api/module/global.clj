(ns low.api.module.global
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add
  ([module type name]
     (LLVM :AddGlobal module type name))
  ([module type name address-space]
     (LLVM :AddGlobalInAddressSpace module type name address-space)))

(defn global [module name]
  (LLVM :GetNamedGlobal module name))

(defn first [module]
  (LLVM :GetFirstGlobal module))

(defn last [module]
  (LLVM :GetLastGlobal module))

(defn next [module]
  (LLVM :GetNextGlobal module))

(defn prev [module]
  (LLVM :GetPreviousGlobal module))

(defn globals [module]
  (lazy-seq (cons (first module)
                  (take-while deref
                              (repeatedly #(next module))))))
