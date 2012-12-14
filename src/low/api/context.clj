(ns low.api.context
  (:require [low.llvm :refer [LLVM]]))

(defn create []
  (LLVM :ContextCreate))

(defn context
  ([]
     (LLVM :GetGlobalContext))
  ([module]
     (LLVM :GetModuleContext module)))

(defn destroy! [context]
  (LLVM :ContextDispose context))

(defmacro with-destroy [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))

(defn metadata-kind-id
  ([string]
     (LLVM :GetMDKindID string (count string)))
  ([context string]
     (LLVM :GetMDKindIDInContext context string (count string))))
