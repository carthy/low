(ns low.api.type
  (:refer-clojure :exclude [type struct])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [array-of pointer to-ptr-vec & type-map]]
            [low.api.context :refer [context]]))

(defn type [t]
  (LLVM :GetTypeKind t))

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
(defn function
  ([return-type [& arg-types]]
     (function return-type arg-types false))
  ([return-type [& arg-types] var-arg?]
     (let [arg-count (count arg-types)]
       (LLVM :FunctionType return-type (array-of :type arg-types) arg-count var-arg?))))

(defn arg-count [function]
  (LLVM :CountParamTypes function))

(defn arg-types [function]
  (let [args-c @(arg-count function)
        ret (pointer :type args-c)]
    (LLVM :GetParamTypes function ret)
    (to-ptr-vec ret args-c)))

(defn return-type [function]
  (LLVM :GetReturnType function))

(defn var-arg? [function]
  (LLVM :IsFunctionVarArg function))

;; struct
(declare opaque-struct struct-body!)

(defn struct
  ([name element-types packed?]
     (create-struct (context) name element-types packed?))
  ([context name element-types packed?]
     (doto (opaque-struct context name)
       (struct-body! element-types packed?))))

(defn opaque-struct
  ([name]
     (opaque-struct (context) name))
  ([context name]
     (LLVM :StructCreateNamed context name)))

(defn struct-body! [struct element-types packed?]
  (LLVM :StructSetBody struct
        (array-of :type element-types)
        (count element-types) packed?))

(defn literal-struct
  ([element-types packed?]
     (LLVM :StructType (array-of :type element-types)
           (count element-types) packed?))
  ([context element-types packed?]
     (LLVM :StructTypeInContext context
           (array-of :type element-types)
           (count element-types) packed?)))

(defn struct-name [t]
  (LLVM :GetStructName t))

(defn packed? [struct]
  (LLVM :IsPackedStruct struct))

(defn opaque? [struct]
  (LLVM :IsOpaqueStruct struct))

(defn element-count [struct]
  (LLVM :CountStructElementTypes struct))

(defn element-types [struct]
  (let [element-c @(element-count struct)
        ret (pointer :type element-c)]
    (LLVM :GetStructElementTypes struct ret)
    (to-ptr-vec ret element-c)))
