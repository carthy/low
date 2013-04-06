(ns low.api.module.variable
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add
  "Adds the global var in the module"
  ([module type name]
     (LLVM :AddGlobal module type name))
  ([module type name address-space]
     (LLVM :AddGlobalInAddressSpace module type name address-space)))

(defn get
  "Gets the global var from the module"
  [module name]
  (LLVM :GetNamedGlobal module name))

(defn first
  "Returns the first global var from the module"
  [module]
  (LLVM :GetFirstGlobal module))

(defn last
  "Returns the last global var from the module"
  [module]
  (LLVM :GetLastGlobal module))

(defn next
  "Returns the next global var"
  [var]
  (LLVM :GetNextGlobal var))

(defn prev
  "Returns the previous global var"
  [var]
  (LLVM :GetPreviousGlobal var))
