(ns low.api.value.constant
  (:refer-clojure :exclude [vector struct])
  (:require [low.llvm :refer [LLVM]]
            [low.utils :refer [camel-case]]))

;; do those belong here?
(defn sizeof [type]
  (LLVM :SizeOf type))

(defn alignof [type]
  (LLVM :AlignOf type))

(defn ^:private LLVMC [op & args]
  (apply LLVM (->> op name (str "Const") keyword) args))

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
         (LLVMC :IntOfArbitraryPrecision type num c))))
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

(def exprs #{:neg :nsw-neg :nuw-neg :f-neg
             :add :nsw-add :nuw-add :f-add
             :sub :nsw-sub :nuw-sub :f-sub
             :mul :nsw-mul :nuw-mul :f-mul
             :u-div :s-div :exact-s-div :f-div
             :u-rem :s-rem :f-rem
             :not :and :or :xor :cmp :f-cmp :i-cmp
             :shl :l-shr :a-shr :get-element-ptr
             :in-bounds-get-element-ptr
             :extract-element :insert-element
             :insert-value :select
             :shuffle-vector :fp-cast :bit-cast :int-cast
             :ptr-cast :s-ext-or-bit-cast :trunc-or-bit-cast
             :z-ext-or-bit-cast :inline-asm
             :fp-ext :fp-to-si :fp-to-ui :fp-trunc :int-to-ptr
             :ptr-to-int :s-ext :si-to-fp :trunc :ui-to-fp
             :z-ext :extract-value :load :var-arg})

(def ^:private special-type-name
  {:nsw-neg "NSWNeg"
   :nuw-neg "NUWNeg"
   :nsw-add "NSWAdd"
   :nuw-add "NUWAdd"
   :nsw-sub "NSWSub"
   :nuw-sub "NUWSub"
   :nsw-mul "NSWMul"
   :nuw-mul "NUWMul"
   :get-element-ptr "GEP"
   :in-bounds-get-element-ptr "InBoundsGEP"
   :ptr-cast "PointerCast"
   :fp-ext "FPExt"
   :fp-to-ui "FPToUI"
   :fp-to-si "FPToSI"
   :fp-trunc "FPTrunc"
   :si-to-fp "SIToFP"
   :ui-to-fp "UIToFP"})


(defn expr [expr & args]
  (apply LLVMC (keyword
                (or (special-type-name expr)
                    (camel-case expr)))
         args))

(defn block-address [f basic-block]
  (LLVM :BlockAddress f basic-block))
