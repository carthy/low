(ns low.api.value.function.basic-block
  (:refer-clojure :exclude [count get first last next])
  (:require [low.llvm :refer [LLVM]]))

(defn count [function]
  (LLVM :CountBasicBlocks function))

(defn get [function idx]
  (LLVM :GetEntryBasicBlock function idx))

(defn first [function]
  (LLVM :GetFirstBasicBlock function))

(defn last [function]
  (LLVM :GetLastBasicBlock function))

(defn append
  ([function name]
     (LLVM :AppendBasickBlock function name))
  ([context function name]
     (LLVM :AppendBasickBlockInContext context function name)))

(defn insert
  ([function name]
     (LLVM :InsertBasickBlock function name))
  ([context function name]
     (LLVM :InsertBasickBlockInContext context function name)))
