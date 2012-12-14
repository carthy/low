(ns low.api.execution-engine
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer &]])
  (:import (com.sun.jna Memory Pointer)))

(defn create [module & [type]]
  {:pre [(or (not type) (#{:jit :interpreter} type))]}
  (let [err (pointer :char*)
        engine (pointer :execution-engine-ref)
        action (if type
                 (if (= type :jit)
                   :CreateJITCompiler
                   :CreateInterpreter)
                 :CreateExecutionEngine)
        action (if (= (:type module) :module-ref)
                 (keyword (str (name action) "ForModule"))
                 action)
        ret (LLVM action  engine module err)]
    (if @ret
      (.getString (& err) 0)
      engine)))

(defn destroy! [engine]
  (LLVM :DisposeExecutionEngine engine))

(defmacro with-destroy [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))
