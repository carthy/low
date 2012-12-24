(ns low.api.value.basic-block
  (:require [low.llvm :refer [LLVM]]))

(defn value [bb]
  (LLVM :BasicBlockAsValue bb))

(defn function [bb]
  (LLVM :GetBasicBlockParent bb))

(defn terminator [bb]
  (LLVM :GetBasicBlockTerminator bb))
