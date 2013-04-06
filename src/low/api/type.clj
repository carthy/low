(ns low.api.type
  (:refer-clojure :exclude [type struct vector])
  (:require [low.llvm :refer [LLVM llvm-version]]
            [low.api.context :as c]
            [low.api.type.integer :as integer]
            [low.api.type.floating :as floating]
            [low.api.type.struct :as struct]))

(defn type
  "Returns the type-kind for a given type"
  [t]
  (LLVM :GetTypeKind t))

(defn sized?
  "Returns true if the type is sised"
  [t]
  (LLVM :TypeIsSized t))

(defn size
  "Returns the size of the type"
  [type]
  (LLVM :SizeOf type))

(defn context
  "Returns the context to which this type instance is associated."
  [t]
  (LLVM :GetTypeContext t))

(defn elements-type
  "Returns the type of elements within a sequential type"
  [sequential]
  (LLVM :GetElementType sequential))

(defn alignof
  "Returns the alignment of the type"
  [type]
  (LLVM :AlignOf type))

(defn integer
  "Returns the an integer type, the default size is 32.
   Size can be specified as a number, or as a keyword.
   See type.integer/types for valid keywords and their sizes"
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

(defn floating
  "Returns the a floating type, the default type is float.
   See type.floating/types for the list of valid types"
  ([] (floating :float))
  ([type]
     {:pre [(floating/types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating/types type) "Type"))))
  ([context type]
     {:pre [(floating/types type)
            (if (= :half type) (>= @llvm-version 3.1) true)]}
     (LLVM (keyword (str (floating/types type) "TypeInContext")) context)))

(defn function
  "Returns a function type with the specified signature"
  ([return-type [& arg-types]]
     (function return-type arg-types false))
  ([return-type [& arg-types] var-arg?]
     (let [arg-count (count arg-types)]
       (LLVM :FunctionType return-type arg-types arg-count var-arg?))))

(declare opaque-struct)

(defn struct
  "Returns a struct with the specified signature"
  ([name element-types packed?]
     (struct (c/context) name element-types packed?))
  ([context name element-types packed?]
     (doto (opaque-struct context name)
       (struct/body! element-types packed?))))

(defn opaque-struct
  "Returns an opaque struct"
  ([name]
     (opaque-struct (c/context) name))
  ([context name]
     (LLVM :StructCreateNamed context name)))

(defn literal-struct
  "Returns an anonymous struct"
  ([element-types packed?]
     (LLVM :StructType element-types
           (count element-types) packed?))
  ([context element-types packed?]
     (LLVM :StructTypeInContext context
           element-types
           (count element-types) packed?)))

(defn array [elements-type length]
  "Returns an array of the specified type and length"
  (LLVM :ArrayType elements-type length))

(defn pointer [element-type address-space]
  "Returns a pointer to the specified type, in the address space"
  (LLVM :PointerType element-type address-space))

(defn vector [elements-type size]
  "Returns a vector of the specified type and size"
  (LLVM :VectorType elements-type size))

(defn void
  "Returns the void type"
  ([] (LLVM :VoidType))
  ([context] (LLVM :VoidTypeInContext context)))

(defn label
  "Returns the label type"
  ([] (LLVM :LabelType))
  ([context] (LLVM :LabelTypeInContext context)))

(defn X86-MMX
  "Returns the X86-MMX type"
  ([] (LLVM :X86MMXType))
  ([context] (LLVM :X86MMXTypeInContext context)))

(defn metadata-kind-id
  "Returns the metadata kind id identified by the string"
  ([string]
     (LLVM :GetMDKindID string (count string)))
  ([context string]
     (LLVM :GetMDKindIDInContext context string (count string))))
