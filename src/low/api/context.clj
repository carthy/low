(ns low.api.context
  (:refer-clojure :exclude [get])
  (:require [low.llvm :refer [LLVM]]))

(defn create []
  "Create a context"
  (LLVM :ContextCreate))

(defn get []
  "Returns the global context"
  (LLVM :GetGlobalContext))

(defn destroy! [context]
  "Destroy the context"
  (LLVM :ContextDispose context))

(defmacro with-destroy [[& ctxs] & body]
  "Execute the body and destroy the context"
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))
