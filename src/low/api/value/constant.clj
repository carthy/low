(ns low.api.value.constant
  (:refer-clojure :exclude [vector])
  (:require [low.llvm :refer [LLVM]]
            [low.native :refer [array-of]]))

;; do those belong here?
(defn sizeof [type]
  (LLVM :SizeOf type))

(defn alignof [type]
  (LLVM :AlignOf type))

(defmacro ^:private LLVMC [op & args]
  `(LLVM ~(->> op name (str "Const") keyword) ~@args))

(defn null [type]
  (LLVMC :Null type))

(defn null-pointer [type]
  (LLVMC :PointerNull type))

(defn all-ones [type]
  (LLVMC :AllOnes type))

(defn undef [type]
  (LLVM :GetUndef type))

(defn integer
  ([type num c]
     (if (number? num)
       (LLVMC :Int type num c)
       (if (string? num)
         (LLVMC :IntOfString type num c)
         (LLVMC :IntOfArbitraryPrecision type num (array-of :__int64 c)))))
  ([type num len radix]
     (LLVMC :IntOfStringAndSize type num len radix)))


(defn floating
  ([type num]
     (if (number? num)
       (LLVMC :Real type num)
       (LLVMC :RealOfString type num)))
  ([type num size]
     (LLVMC :RealOfStringAndSize type num size)))

(defn int-z-ext-value [value]
  (LLVMC :IntGetZExtValue value))

(defn int-s-ext-value [value]
  (LLVMC :IntGetSExtValue value))

(defn string
  ([string len null-term?]
     (LLVMC :String string len null-term?))
  ([context string len null-term?]
     (LLVMC :StringInContext context string len null-term?)))

(defn struct
  ([vals count packed?]
     (LLVMC :Struct vals count packed?))
  ([context vals count packed?]
     (LLVMC :StructInContext context vals count packed?)))

(defn named-struct [type vals count]
  (LLVMC :NamedStruct type vals count))

(defn array [type vals length]
  (LLVMC :Array type vals length))

(defn vector [vals size]
  (LLVMC :Vector vals size))
