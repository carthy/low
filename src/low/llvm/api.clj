(defenum attribute
  (zipmap (map #(bit-shift-left (or ({16 31 26 7} %) 1) %) (range 32))
          [:z-ext :s-ext :no-return :in-reg :struct-ret
           :no-unwind :no-alias :by-val :nest :read-none
           :read-only :no-inline :always-inline
           :optimize-for-size :stack-protect
           :stack-protect-req :alignment :no-capture
           :no-red-zone :no-implicit-float :naked
           :inline-hint :stack-alignment :returns-twice
           :uw-table :non-lazy-bind]))

(defenum type-kind
  #{3.0}
  [:void :float :double :X86FP80 :FP128 :PPC_FP128
   :label :integer :function :struct :array
   :pointer :opaque :vector :metadata :X86_MMX]
  #{3.1}
  [:void :half :float :double :X86_FP80 :FP128
   :PPC_FP128 :label :integer :function :struct
   :array :pointer :vector :metadata :X86_MMX])

;; we don't support :unwind either for 3.0
(defenum opcode
  [nil :ret :br :switch :indirect-br :invoke
   nil :unreachable :add :fadd :sub :fsub
   :mul :fmul :udiv :sdiv :fdiv :urem :srem
   :frem :shl :lshr :ashr :and :or :xor :alloca
   :load :store :get-element-ptr :trunc :zext
   :sext :fp-to-ui :fp-to-si :ui-to-fp :si-to-fp
   :fp-trunc :fp-ext :ptr-to-int :int-to-ptr
   :bit-cast :icmp :fcmp :phi :call :select
   :user-op1 :user-op2 :va-arg :extract-element
   :insert-element :shuffle-vector :extract-value
   :insert-value :fence :atomic-cmp-xchg
   :atomic-rmw :resume :landing-pad])

(defenum linkage
  [:external :available-externally :link-once-any
   :link-once-odr :weak-any :weak-odr :appending
   :internal :private :dll-import :dll-export
   :external-weak :ghost :common :linker-private
   :linker-private-weak :linker-private-weak-def-auto])

(defenum visibility
  [:default :hidden :protected])

(defenum call-conv
  {0 :c 8 :fast 9 :cold 10 :ghc 11 :hipe
   64 :x86-std 65 :x86-fast 66 :arm-apcs
   67 :arm-aapcs 68 :arm-aapcs-vfp 69 :msp430-intr
   70 :x86-this-call 71 :ptx-kernel 72 :ptx-device
   73 :mblaze-interrupt 74 :mblaze-svol
   75 :spir-func 76 :spir-func 77 :intel-ocl-bi})

(defenum int-predicate
  (zipmap (range 32 42) [:eq :ne :ugt :uge :ult
                         :ule :sgt :sge :slt :sle]))

(defenum real-predicate
  [false :oeq :ogt :oge :olt :ole :one :ord
   :uno :ueq :ugt :uge :ult :ule :une true])

(defpointers
  context-ref)

(def ^:private llvm-api
  [;; Context
   [:ContextCreate [] :context-ref]
   [:GetGlobalContext [] :context-ref]
   [:ContextDispose [:context-ref] :void]
   [:GetMDKindIDInContext [:context-ref :constchar :unsigned] :unsigned]
   [:GetMDKindID [:constchar :unsigned] :unsigned]])
