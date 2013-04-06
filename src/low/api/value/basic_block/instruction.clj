(ns low.api.value.basic-block.instr
  (:refer-clojure :exclude [first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn first
  "Returns the first instruction of the basic block"
  [bb]
  (LLVM :GetFirstInstruction bb))

(defn last
  "Returns the last instruction of the basic block"
  [bb]
  (LLVM :GetLastInstruction bb))

(defn next
  "Returns the next instruction of the basic block"
  [bb]
  (LLVM :GetNextInstruction bb))

(defn prev
  "Returns the previous instruction of the basic block"
  [bb]
  (LLVM :GetPreviousInstruction bb))
