(ns low.api.type.integer
  (:require [low.llvm :refer [LLVM]]))

(def types
  "A map of valid integer types"
  {:bool 1
   :byte 8
   :short 16
   :int 32
   :long 64})

(defn width [int-type]
  "Returns the width of an integer type"
  (LLVM :GetIntTypeWidth int-type))
