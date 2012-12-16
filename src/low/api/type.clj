(ns low.api.type
  (:refer-clojure :exclude [type struct vector])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.native :refer [array-of]]
            [low.api.context :as c]
            [low.api.type.integer :as integer]
            [low.api.type.floating :as floating]
            [low.api.type.struct :as struct]))

(defn type [t]
  (LLVM :GetTypeKind t))

(defn sized? [t]
  (LLVM :TypeIsSized t))

(defn context [t]
  (LLVM :GetTypeContext t))

(defn elements-type [sequential]
  (LLVM :GetElementType sequential))

;; integer
(defn integer
  ([] (LLVM :Int32Type))
  ([bits-or-context]
     (if (number? bits-or-context)
       (if ((set (vals integer/types)) bits-or-context)
         (LLVM (keyword (str "Int" bits-or-context "Type")))
         (LLVM :IntType bits-or-context))
       (if (keyword? bits-or-context)
         (let [n (integer/types bits-or-context)]
           (assert n)
           (integer n))
         (LLVM :Int32TypeInContext bits-or-context))))
  ([context bits]
     (if (number? bits)
       (if ((set (vals integer/types)) bits)
         (LLVM (keyword (str "Int" bits "TypeInContext") context))
         (LLVM :IntType context bits))
       (let [n (integer/types bits)]
         (assert n)
         (integer context n)))))

;; floating point
(defn floating
  ([type]
     {:pre [(floating/types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating/types type) "Type"))))
  ([context type]
     {:pre [(floating/types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating/types type) "TypeInContext")) context)))

;; function
(defn function
  ([return-type [& arg-types]]
     (function return-type arg-types false))
  ([return-type [& arg-types] var-arg?]
     (let [arg-count (count arg-types)]
       (LLVM :FunctionType return-type (array-of :type arg-types) arg-count var-arg?))))

;; struct
(declare opaque-struct)

(defn struct
  ([name element-types packed?]
     (create-struct (c/context) name element-types packed?))
  ([context name element-types packed?]
     (doto (opaque-struct context name)
       (struct/body! element-types packed?))))

(defn opaque-struct
  ([name]
     (opaque-struct (c/context) name))
  ([context name]
     (LLVM :StructCreateNamed context name)))

(defn literal-struct
  ([element-types packed?]
     (LLVM :StructType (array-of :type element-types)
           (count element-types) packed?))
  ([context element-types packed?]
     (LLVM :StructTypeInContext context
           (array-of :type element-types)
           (count element-types) packed?)))

;; array
(defn array [elements-type length]
  (LLVM :ArrayType elements-type length))

;; pointer
(defn pointer [element-type adress-space]
  (LLVM :PointerType element-type adress-space))

;; vector
(defn vector [elements-type length]
  (LLVM :VectorType elements-type length))
