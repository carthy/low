(ns low.jna
  (:import com.sun.jna.Function))

(defmacro import-jna-function [lib f-name ret-type args-len]
  "Creates (only if it has not already been created) a clojure function
that invokes the matching function from lib
Example: (import-jna-function LLVM-3.2 LLVMTypeOf com.sun.jna.Pointer 1)"
  (let [args (repeatedly args-len gensym)]
    `(let [f# (Function/getFunction ~(str lib) ~(str f-name))]
       (defonce ~f-name
         (fn [~@args]
           (.invoke f# ~ret-type (to-array '~args)))))))

