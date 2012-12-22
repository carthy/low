(ns low.api.value.metadata
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [pointer to-str]]))

(defn string
  ([str]
     (LLVM :MDString str (count str)))
  ([context str]
     (LLVM :MDStringInContext str (count str))))

(defn to-string [md-string]
  (let [len (pointer :int)] ;; do we even need this?
    (LLVM :GetMDString md-string len)))

(defn node
  ([vals]
     (LLVM :MDNode vals (count vals)))
  ([context vals]
     (LLVM :MDNodeInContext vals (count vals))))
