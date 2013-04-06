(ns low.api.value.basic-block
  (:require [low.llvm :refer [LLVM]]
            [clojure.string :as s]
            [low.api.value.basic-block.instruction :as instr]))

(defn value
  "Returns the basic block as a value"
  [bb]
  (LLVM :BasicBlockAsValue bb))

(defn function
  "Returns the function with which the basic block is associated"
  [bb]
  (LLVM :GetBasicBlockParent bb))

(defn terminator
  "Returns the basic block terminator"
  [bb]
  (LLVM :GetBasicBlockTerminator bb))

(defn delete!
  "Deletes a basic block"
  [bb]
  (LLVM :DeleteBasicBlock bb))

(defn move!
  "Moves a basic block before or after another, depending on the value of direction
   direction can be either :after or :before"
  [direction from to]
  {:pre [(#{:after :before} direction)]}
  (LLVM (keyword (str "MoveBasicBlock" (s/capitalize (name direction))))
        from to))

(defn instructions
  "Returns a lazy seq of the instructions in the basic block"
  [bb]
  (let [first-instr (instr/first bb)]
    (lazy-seq (cons first-instr
                    (take-while deref
                                (iterate instr/next first-instr))))))
