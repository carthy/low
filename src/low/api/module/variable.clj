(ns low.api.module.variable
  (:refer-clojure :exclude [get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn add
  "Add the global var in the module"
  ([module type name]
     (LLVM :AddGlobal module type name))
  ([module type name address-space]
     (LLVM :AddGlobalInAddressSpace module type name address-space)))

(defn get [module name]
  "Get the global var from the module"
  (LLVM :GetNamedGlobal module name))

(defn first [module]
  "Return the first global var from the module"
  (LLVM :GetFirstGlobal module))

(defn last [module]
  "Return the last global var from the module"
  (LLVM :GetLastGlobal module))

(defn next [var]
  "Return the next global var"
  (LLVM :GetNextGlobal var))

(defn prev [var]
  "Return the previous global var"
  (LLVM :GetPreviousGlobal var))
