(ns low.api.type
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer & to-str]]))

;; integer
(def ^:private integer-types
  {:bool 1
   :byte 8
   :short 16
   :int 32
   :long 64})

(defn integer
  ([] (LLVM :Int32Type))
  ([bits-or-context]
     (if (number? bits-or-context)
       (if (#{1 8 16 32 64} bits)
         (LLVM (keyword "Int" bits "Type"))
         (LLVM :IntType bits))
       (if (keyword? bits-or-context)
         (let [n (integer-types bits-or-context)]
           (assert n)
           (integer n))
         (LLVM :Int32TypeInContext bits-or-context))))
  ([context bits]
     (if (number? bits)
       (if (#{1 8 16 32 64} bits)
         (LLVM (keyword "Int" bits "TypeInContext" context))
         (LLVM :IntType context bits))
       (let [n (integer-types bits)]
         (assert n)
         (integer context n)))))

(defn width [int-type]
  (LLVM :GetIntTypeWidth int-type))

;; floating point
(def ^:private floating-types
  {:half "Half"
   :float "Float"
   :double "Double"
   :X86-FP80 "X86FP80"
   :FP128 "FP128"
   :PPC-FP128 "PPCFP128"})

(defn floating
  ([type]
     {:pre [(floating-types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating-types type) "Type"))))
  ([context type]
     {:pre [(floating-types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating-types type) "TypeInContext")) context)))
