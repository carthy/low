(ns low.api.value.basic-block
  (:refer-clojure :exclude [first last])
  (:require [low.llvm :refer [LLVM]]
            [clojure.string :as s]))

(defn value [bb]
  (LLVM :BasicBlockAsValue bb))

(defn function [bb]
  (LLVM :GetBasicBlockParent bb))

(defn terminator [bb]
  (LLVM :GetBasicBlockTerminator bb))

(defn next [bb]
  (LLVM :GetNextBasicBlock bb))

(defn prev [bb]
  (LLVM :GetPreviousBasicBlock bb))

(defn delete [bb]
  (LLVM :DeleteBasicBlock bb))

(defn move [direction from to]
  {:pre [(#{:after :before} direction)]}
  (LLVM (keyword (str "MoveBasicBlock" (s/capitalize (name direction))))
        from to))

;; not the best name
(defn first [bb]
  (LLVM :GetFirstInstruction bb))

(defn last [bb]
  (LLVM :GetLastInstruction bb))
