(ns low.jna
  (:import com.sun.jna.Function))

(defn import-jna-function [lib f-name ret-type args-len]
  "Returns a  clojure function that invokes the matching function from lib.
Example: (import-jna-function \"LLVM-3.2\" \"LLVMTypeOf\" com.sun.jna.Pointer 1)"
  (let [f (Function/getFunction lib f-name)]
    (fn [& args]
      (assert (== args-len (count args)))
      (.invoke f ret-type (to-array args)))))
