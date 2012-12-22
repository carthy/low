(ns low.api.execution-engine
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-str &]])
  (:import (com.sun.jna Memory Pointer)))

(defn create [module & [type opt-level]]
  {:pre [(or (not type) (#{:jit :interpreter} type))]}
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
    (if @ret
      (to-str (& err))
      engine)))

(defn destroy! [engine]
  (LLVM :DisposeExecutionEngine engine))

(defmacro with-destroy [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))
