(ns low.api.type
  (:refer-clojure :exclude [int float double])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [pointer & to-str]]))

;; integer
(defn int
  ([] (LLVM :Int32Type))
  ([bits-or-context]
     (if (number? bits-or-context)
       (if (#{1 8 16 32} bits)
         (LLVM (keyword "Int" bits "Type"))
         (LLVM :IntType bits))
       (LLVM :Int32TypeInContext bits-or-context)))
  ([context bits]
     (if (#{1 8 16 32} bits)
       (LLVM (keyword "Int" bits "TypeInContext" context))
       (LLVM :IntType context bits))))

(defn width [int-type]
  (LLVM :GetIntTypeWidth int-type))

;; floating point
(defn half
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :HalfType))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :HalfTypeInContext context)))

(defn float
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :FloatType))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :FloatTypeInContext context)))

(defn double
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :DoubleType))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :DoubleTypeInContext context)))

(defn X86-FP80
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :X86FP80Type))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :X86FP80TypeInContext context)))

(defn FP128
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :FP128Type))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :FP128TypeInContext context)))

(defn PPC-FP128
  ([]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :PPCFP128Type))
  ([context]
     {:pre [(>= @llvm-version 3.1)]}
     (LLVM :PPCFP128TypeInContext context)))
