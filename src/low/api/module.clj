(ns low.api.module
  (:require [low.llvm :refer [LLVM]]))

(defn create
  ([^String name]
     (LLVM :ModuleCreateWithName name))
  ([^String name context]
     (LLVM :ModuleCreateWithNameInContext name context)))

(defn destroy! [module]
  (LLVM :DisposeModule module))

(defmacro with-modules [[& ctxs] & body]
  `(let [~@(mapcat #(list % `(create)) ctxs)]
     (try ~@body
          (finally ~@(map #(list `destroy! %) ctxs)))))

(defn data-layout [module]
  (LLVM :GetDataLayout module))

(defn data-layout! [module triple]
  (LLVM :SetDataLayout module triple))

(defn target [module]
  (LLVM :GetTarget module))

(defn target! [module triple]
  (LLVM :SetTarget module triple))

(defn dump! [module]
  (LLVM :DumpModule module))

(defn inline-asm! [module asm]
  (LLVM :SetModuleInlineAsm module asm))

(defn context [module]
  (LLVM :GetModuleContext module))

(defn type [module name]
  (LLVM :GetTypeByName module name))

(defn add-function [module name function]
  (LLVM :AddFunction module name function))

(defn function [module name]
  (LLVM :GetNamedFunction module name))

(defn first-function [module]
  (LLVM :GetFirstFunction module))

(defn last-function [module]
  (LLVM :GetLastFunction module))

(defn add-global
  ([module type name]
     (LLVM :AddGlobal module type name))
  ([module type name address-space]
     (LLVM :AddGlobalInAddressSpace module type name address-space)))

(defn global [module name]
  (LLVM :GetNamedGlobal module name))

(defn first-global [module]
  (LLVM :GetFirstGlobal module))

(defn last-global [module]
  (LLVM :GetLastGlobal module))

(defn add-alias [module type value name]
  (LLVM :AddAlias module type value name))
