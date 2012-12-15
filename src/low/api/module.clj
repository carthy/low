(ns low.api.module
  (:refer-clojure :exclude [type])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer & to-str]]))

(defn create
  ([^String name]
     (LLVM :ModuleCreateWithName name))
  ([^String name context]
     (LLVM :ModuleCreateWithNameInContext name context)))

(defn destroy! [module]
  (LLVM :DisposeModule module))

(defmacro with-destroy [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))

(defn target [module]
  (LLVM :GetTarget module))

(defn target! [module triple]
  (LLVM :SetTarget module triple))

(defn data-layout [module]
  (LLVM :GetDataLayout module))

(defn data-layout! [module triple]
  (LLVM :SetDataLayout module triple))

(defn link [module module-to-link preserv?]
  {:pre [(>= @llvm-version 3.2)]
   (let [out (pointer :char*)]
     (assoc (LLVM :LinkModules module module-to-link preserv? out)
       :out out))})

(defn dump! [module]
  (LLVM :DumpModule module))

(defn inline-asm! [module asm]
  (LLVM :SetModuleInlineAsm module asm))

(defn context [module]
  (LLVM :GetModuleContext module))

(defn type [module name]
  (LLVM :GetTypeByName module name))

(defn add-alias [module type value name]
  (LLVM :AddAlias module type value name))

(defn verify [module failure-action]
  (let [err (pointer :char*)]
    (assoc (LLVM :VerifyModule module failure-action err)
      :err (& err))))

;; functions
(defn add-function [module name function]
  (LLVM :AddFunction module name function))

(defn function [module name]
  (LLVM :GetNamedFunction module name))

(defn first-function [module]
  (LLVM :GetFirstFunction module))

(defn last-function [module]
  (LLVM :GetLastFunction module))

(defn next-function [module]
  (LLVM :GetNextFunction module))

(defn prev-function [module]
  (LLVM :GetPreviousFunction module))

(defn functions [module]
  (lazy-seq (cons (first-function module)
                  (take-while #(not= (last-function module %))
                              (repeatedly #(next-function module))))))

;; global variables
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

(defn next-global [module]
  (LLVM :GetNextGlobal module))

(defn prev-global [module]
  (LLVM :GetPreviousGlobal module))

(defn globals [module]
  (lazy-seq (cons (first-global module)
                  (take-while #(not= (last-global module %))
                              (repeatedly #(next-global module))))))
