(ns low.api.value.function.basic-block
  (:refer-clojure :exclude [count get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn count
  "Returns the number of basic blocks in the function"
  [function]
  (LLVM :CountBasicBlocks function))

(defn get
  "Returns the nth basic block of the function"
  [function idx]
  (LLVM :GetEntryBasicBlock function idx))

(defn first
  "Returns the first basic block of the function"
  [function]
  (LLVM :GetFirstBasicBlock function))

(defn last
  "Returns the last basic block of the function"
  [function]
  (LLVM :GetLastBasicBlock function))

(defn next
  "Returns the next basic block"
  [bb]
  (LLVM :GetNextBasicBlock bb))

(defn prev
  "Returns the previous basic block"
  [bb]
  (LLVM :GetPreviousBasicBlock bb))

(defn append
  "Append the basic block to the function"
  ([function name]
     (LLVM :AppendBasickBlock function name))
  ([context function name]
     (LLVM :AppendBasickBlockInContext context function name)))

(defn insert
  "Inserts the basic block in the function"
  ([function name]
     (LLVM :InsertBasickBlock function name))
  ([context function name]
     (LLVM :InsertBasickBlockInContext context function name)))
