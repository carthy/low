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

(defn destroy!
  "Destroy the module"
  [module]
  (LLVM :DisposeModule module))

(defmacro with-destroy
  "Execute the body and destroy the module"
  [[& mds] & body]
  `(let [~@mds]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 mds))))))

(defn target
  "Get the module target"
  [module]
  (LLVM :GetTarget module))

(defn target!
  "Set the module target"
  [module triple]
  (LLVM :SetTarget module triple))

(defn data-layout
  "Get the module data layout"
  [module]
  (LLVM :GetDataLayout module))

(defn data-layout!
  "Set the module data layout"
  [module ^String triple]
  (LLVM :SetDataLayout module triple))

(defn link [module module-to-link preserve?]
  "Link two modules, the output can be obtained by looking-up :out
   in the returned NativeValue record"
  {:pre [(>= @llvm-version 3.2)]}
  (let [out (pointer :char*)]
    (assoc (LLVM :LinkModules module module-to-link preserev? out)
      :out out)))

(defn dump!
  "Dump a module"
  [module]
  (LLVM :DumpModule module))

(defn inline-asm!
  "Set the module-scope inline assembly blocks"
  [module ^String asm]
  (LLVM :SetModuleInlineAsm module asm))

(defn context
  "Get the context of the module"
  [module]
  (LLVM :GetModuleContext module))

(defn type
  "Returns the type with the name `name` in the module"
  [module ^String name]
  (LLVM :GetTypeByName module name))

(defn verify [module failure-action]
  "Verify a module, the error output can be optained by looking-up :err
   in the returned NativeValue record"
  (let [err (pointer :char*)]
    (assoc (LLVM :VerifyModule module failure-action err)
      :err err)))

(defn functions
  "Return a lazy seq of the functions of the module"
  [module]
  (let [first-f (f/first module)]
    (lazy-seq (cons first-f
                    (take-while deref
                                (iterate f/next first-f))))))

(defn variables
  "Return a lazy seq of the global vars of the module"
  [module]
  (let [first-v (v/first module)]
    (lazy-seq (cons first-v
                    (take-while deref
                                (iterate v/next first-v))))))

(defn operands
  "Return a vector containing the named metadata operands for the module"
  [module name]
  {:pre [(>= @llvm-version 3.2)]}
  (let [count @(m/count module name)
        ret (pointer :value count)]
    (LLVM :GetNamedMetadataOperands module name ret)
    (ptr-to-vec ret count)))
