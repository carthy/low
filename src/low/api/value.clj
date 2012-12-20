(ns low.api.value
  (:refer-clojure :exclude [type isa? name cast])
  (:require [clojure.core :as c.c]
            [low.llvm :refer [LLVM llvm-version]]
            [low.utils :refer [camel-case]]))

(def types #{:argument :basic-block :inline-asm :metadata-node :metadata-string
             :user :constant :block-address :constant-aggregate-zero :constant-array
             :constant-expr :constant-fp :constant-int :constant-pointer-null
             :constant-vector :global-value :function :global-alias :global-variable
             :undef-value :instruction :binary-operator :call-inst :intrinsic-inst
             :debug-info-intrinsic :debug-declare-inst :mem-intrinsic :mem-copy-inst
             :mem-move-inst :mem-set-inst :cmp-inst :f-cmp-inst :i-cmp-inst
             :extract-element-inst :get-element-ptr-inst :insert-element-inst
             :insert-value-inst :landing-pad-inst :phi-node :select-inst
             :shuffle-vector-inst :store-inst :terminator-inst :branch-inst
             :indirect-br-inst :invoke-inst :return-inst :switch-inst :unreachable-inst
             :resume-inst :unary-instruction :alloca-inst :cast-inst :bit-cast-inst
             :fp-ext-inst :fp-to-si-inst :fp-to-ui-inst :fp-trunc-inst :int-to-ptr-inst
             :ptr-to-int-inst :s-ext-inst :si-to-fp-inst :trunc-inst :ui-to-fp-inst
             :z-ext-inst :extract-value-inst :load-inst :var-arg-inst})

(def ^:private special-type-name
  {:metadata-node "MDNode"
   :metadata-string "MDString"
   :constant-fp "ConstantFP"
   :debug-declare-inst "DbgDeclareInst"
   :debug-info-intrinsic "DbgInfoIntrinsic"
   :mem-copy-inst "MemCpyInst"
   :phi-node "PHINode"
   :fp-ext-inst "FPExtInst"
   :fp-to-ui-inst "FPToUIInst"
   :fp-to-si-inst "FPToSIInst"
   :fp-trunc-inst "FPTruncInst"
   :si-to-fp-inst "SIToFPInst"
   :ui-to-fp-inst "UIToFPInst"
   :var-arg-inst "VAArgInst"})

(defn isa? [value type]
  (LLVM (keyword (str "IsA"
                      (or (special-type-name type)
                          (c.c/name (camel-case type)))))
        value))

(defn cast [value type]
  (isa? value type))

(defn type [value]
  (LLVM :TypeOf value))

(defn name! [value name]
  (LLVM :SetValueName value name))

(defn name [value]
  (LLVM :GetValueName value))

(defn dump [value]
  (LLVM :DumpValue value))

(defn replace-uses [old-val new-val]
  (LLVM :ReplaceAllUsesWith old-val new-val))

(defn constant? [value]
  (LLVM :IsConstant value))

(defn defined? [value]
  (not (LLVM :IsUndef value)))

(defn null? [value]
  (LLVM :IsNull value))
