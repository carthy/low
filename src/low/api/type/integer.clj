(ns low.api.type.integer
  (:require [low.llvm :refer [LLVM]]))

(def types
  {:bool 1
   :byte 8
   :short 16
   :int 32
   :long 64})

(defn width [int-type]
  (LLVM :GetIntTypeWidth int-type))
