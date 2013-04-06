(ns low.api.execution-engine
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-str]])
  (:import (com.sun.jna Memory Pointer)))

(defn create
  "Creates an execution engine, if type is specified, a JIT compiler
   or an interpreter will be created, type can be :jit or :interpreter
   If type is :jit, an opt-level can be specified, see the opt-level enum"
  [module & [type opt-level]]
  {:pre [(or (not type)
             (#{:jit :interpreter} type))]}
  (let [err (pointer :char*)
        engine (pointer :execution-engine)
        action (if type
                 (if (= type :jit)
                   :CreateJITCompiler
                   :CreateInterpreter)
                 :CreateExecutionEngine)
        opt-level (or opt-level :default)
        action (if (= (:type module) :module)
                 (keyword (str (name action) "ForModule"))
                 action)
        ret (apply LLVM action engine module (if (= type :jit) [opt-level err] [err]))]
    (conj engine
          (when @ret
            [:err (to-str err)]))))

(defn destroy!
  "Destroys the execution engine"
  [engine]
  (LLVM :DisposeExecutionEngine engine))

(defmacro with-destroy
  "Executes the body and destroys the execution engine"
  [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))
