(ns low.api.value.metadata
  (:require [low.llvm :refer [LLVM]]))

(defn string
  ([str]
     (LLVM :MDString str (count str)))
  ([context str]
     (LLVM :MDStringInContext str (count str))))

(defn node
  ([vals]
     (LLVM :MDNode vals (count vals)))
  ([context vals]
     (LLVM :MDNodeInContext vals (count vals))))
