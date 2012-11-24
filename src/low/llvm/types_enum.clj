(ns low.llvm.types-enum)

(def byte-ordering
  [:big-endian :little-endian])

(def types-kind
  [:void :half :float :double :X86FP80 :FP128
   :PPC_FP128 :label :integer :function :struct
   :array :pointer :opaque :vector :metadata :X86_MMX])

(def opcode
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

(def linkage
  [:external :available-external :link-once-any
   :link-once-odr :waek-any :weak-odf :appending
   :internal :private :dll-import :dll-export
   :external-weak :ghost :common :linker-private
   :linker-private-weak :linkage-private-weak-def-auto])

(def visibility
  [:default :hidden :protected])

(def instruction-call-conv
  [:C :fast :cold :x86-std :x86-fast])

(def attribute
  (zipmap (concat (map (partial bit-shift-left 1) (range 16))
                  [2031616 2097152 4194304 8388608 16777216
                   33554432 469762048 536870912 1073741824 2147483648])
          [:z-ext :s-ext :no-return :in-reg :struct-ret
           :no-unwind :no-alias :by-val :nest :read-none
           :read-only :no-inline :always-inline
           :optimize-for-size :stack-protect
           :stack-protect-req :alignment :no-capture
           :no-red-zone :no-implicit-float :naked
           :inline-hint :stack-alignment :returns
           :uw-table :non-lazy-bind]))
