(ns low.api.value.function.argument
  (:refer-clojure :exclude [count get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn count
  "Returns the number of arguments of the function"
  [function]
  (LLVM :CountParams function))

(defn get
  "Return the nth argument of the function"
  [function idx]
  (LLVM :GetParam function idx))

(defn first
  "Returns the first argument of the function"
  [function]
  (LLVM :GetFirstParam function))

(defn last
  "Returns the last argument of the function"
  [function]
  (LLVM :GetLastParam function))

(defn next
  "Returns the next argument of the function"
  [function]
  (LLVM :GetNextParam function))

(defn prev
  "Returns the previous argument of the function"
  [function]
  (LLVM :GetPreviousParam function))

(defn alignment!
  "Sets the argument alignment"
  [param alignment]
  (LLVM :SetParamAlignment param alignment))

(defn function
  "Returns the function to which the artument belongs"
  [param]
  (LLVM :GetParamParent param))

;; attr
(defn get-attr
  "Get the parameter attribute"
  [parameter]
  (LLVM :GetAttribute parameter))

(defn add-attr
  "Add an attribute to the parameter"
  [parameter attr]
  (LLVM :AddAttribute parameter attr))

(defn delete!-attr
  "Deletes the parameter attribute"
  [parameter attr]
  (LLVM :RemoveAttribute parameter attr))
