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
  {:pre [(>= @llvm-version 3.2)]}
  (let [out (pointer :char*)]
    (assoc (LLVM :LinkModules module module-to-link preserv? out)
      :out out)))

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
      :err (to-str (& err)))))
