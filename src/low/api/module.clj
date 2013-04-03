(ns low.api.module
  (:refer-clojure :exclude [type])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer & to-str ptr-to-vec]]
            [low.api.module.function :as f]
            [low.api.module.variable :as v]
            [low.api.module.named-metadata-operand :as m]))

(defn create
  "Create a module with the given name in the global context, or, if provided
   in the specified context"
  ([^String name]
     (LLVM :ModuleCreateWithName name))
  ([^String name context]
     (LLVM :ModuleCreateWithNameInContext name context)))

(defn destroy! [module]
  "Destroy the module"
  (LLVM :DisposeModule module))

(defmacro with-destroy [[& mds] & body]
  "Execute the body and destroy the module"
  `(let [~@mds]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 mds))))))

(defn target [module]
  "Get the module target"
  (LLVM :GetTarget module))

(defn target! [module triple]
  "Set the module target"
  (LLVM :SetTarget module triple))

(defn data-layout [module]
  "Get the module data layout"
  (LLVM :GetDataLayout module))

(defn data-layout! [module ^String triple]
  "Set the module data layout"
  (LLVM :SetDataLayout module triple))

(defn link [module module-to-link preserve?]
  "Link two modules, the output can be obtained by looking-up :out
   in the returned NativeValue record"
  {:pre [(>= @llvm-version 3.2)]}
  (let [out (pointer :char*)]
    (assoc (LLVM :LinkModules module module-to-link preserev? out)
      :out out)))

(defn dump! [module]
  "Dump a module"
  (LLVM :DumpModule module))

(defn inline-asm! [module ^String asm]
  "Set the module-scope inline assembly blocks"
  (LLVM :SetModuleInlineAsm module asm))

(defn context [module]
  "Get the context of the module"
  (LLVM :GetModuleContext module))

(defn type [module ^String name]
  "Returns the type with the name `name` in the module"
  (LLVM :GetTypeByName module name))

(defn verify [module failure-action]
  "Verify a module, the error output can be optained by looking-up :err
   in the returned NativeValue record"
  (let [err (pointer :char*)]
    (assoc (LLVM :VerifyModule module failure-action err)
      :err err)))

(defn functions [module]
  "Return a lazy seq of the functions of the module"
  (let [first-f (f/first module)]
    (lazy-seq (cons first-f
                    (take-while deref
                                (iterate f/next first-f))))))

(defn variables [module]
  "Return a lazy seq of the global vars of the module"
  (let [first-v (v/first module)]
    (lazy-seq (cons first-v
                    (take-while deref
                                (iterate v/next first-v))))))

(defn operands [module name]
  "Return a vector containing the named metadata operands for the module"
  {:pre [(>= @llvm-version 3.2)]}
  (let [count @(m/count module name)
        ret (pointer :value count)]
    (LLVM :GetNamedMetadataOperands module name ret)
    (ptr-to-vec ret count)))
