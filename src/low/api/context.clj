(ns low.api.context
  (:refer-clojure :exclude [get])
  (:require [low.llvm :refer [LLVM]]))

(defn create
  "Creates a context"
  []
  (LLVM :ContextCreate))

(defn get
  "Returns the global context"
  []
  (LLVM :GetGlobalContext))

(defn destroy!
  "Destroy the context"
  [context]
  (LLVM :ContextDispose context))

(defmacro with-destroy
  "Executes the body and destroys the context"
  [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))
