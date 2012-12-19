(ns low.api.value.metadata
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [array-of]]))

(defn string
  ([str]
     (LLVM :MDString str (count str)))
  ([context str]
     (LLVM :MDStringInContext str (count str))))

(defn node
  ([vals]
     (LLVM :MDNode (array-of :value vals) (count vals)))
  ([context vals]
     (LLVM :MDNodeInContext (array-of :value vals) (count vals))))
