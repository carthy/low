(ns low.api.type.function
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-ptr-vec]]))

(defn arg-count
  "Return the number of arguments for the function type"
  [function]
  (LLVM :CountParamTypes function))

(defn arg-types
  "Return a vector containing the argument types of the function type"
  [function]
  (let [args-c @(arg-count function)
        ret (pointer :type args-c)]
    (LLVM :GetParamTypes function ret)
    (to-ptr-vec ret args-c)))

(defn return-type
  "Return the return type of the function type"
  [function]
  (LLVM :GetReturnType function))

(defn var-arg?
  "Return true if the function type has var-args"
  [function]
  (LLVM :IsFunctionVarArg function))
