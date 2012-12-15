(ns low.api.type.function
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn arg-count [function]
  (LLVM :CountParamTypes function))

(defn arg-types [function]
  (let [args-c @(arg-count function)
        ret (pointer :type args-c)]
    (LLVM :GetParamTypes function ret)
    (to-ptr-vec ret args-c)))

(defn return-type [function]
  (LLVM :GetReturnType function))

(defn var-arg? [function]
  (LLVM :IsFunctionVarArg function))
