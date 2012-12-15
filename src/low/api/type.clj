(ns low.api.type
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [array-of pointer to-ptr-vec & type-map]]))

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
       (if (#{1 8 16 32 64} bits-or-context)
         (LLVM (keyword (str "Int" bits-or-context "Type")))
         (LLVM :IntType bits-or-context))
       (if (keyword? bits-or-context)
         (let [n (integer-types bits-or-context)]
           (assert n)
           (integer n))
         (LLVM :Int32TypeInContext bits-or-context))))
  ([context bits]
     (if (number? bits)
       (if (#{1 8 16 32 64} bits)
         (LLVM (keyword (str "Int" bits "TypeInContext") context))
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

;; function
(defn function [return-type arg-types var-arg?]
  (let [arg-count (count arg-types)]
    (LLVM :FunctionType return-type (array-of :type-ref arg-types) arg-count var-arg?)))

(defn arg-count [function]
  (LLVM :CountParamTypes function))

(defn arg-types [function]
  (let [ret (pointer :type-ref)]
    (LLVM :GetParamTypes function ret)
    (to-ptr-vec ret @(arg-count function))))

(defn return-type [function]
  (LLVM :GetReturnType function))

(defn var-arg? [function]
  (LLVM :IsFunctionVarArg function))

(defn type? [t]
  (LLVM :GetTypeKind t))
