(ns low.api.module.variable
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add
  "Add the global var in the module"
  ([module type name]
     (LLVM :AddGlobal module type name))
  ([module type name address-space]
     (LLVM :AddGlobalInAddressSpace module type name address-space)))

(defn get
  "Get the global var from the module"
  [module name]
  (LLVM :GetNamedGlobal module name))

(defn first
  "Return the first global var from the module"
  [module]
  (LLVM :GetFirstGlobal module))

(defn last
  "Return the last global var from the module"
  [module]
  (LLVM :GetLastGlobal module))

(defn next
  "Return the next global var"
  [var]
  (LLVM :GetNextGlobal var))

(defn prev
  "Return the previous global var"
  [var]
  (LLVM :GetPreviousGlobal var))
