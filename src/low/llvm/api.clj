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
  [:void :float :double :X86-FP80 :FP128 :PPC-FP128
   :label :integer :function :struct :array
   :pointer :opaque :vector :metadata :X86-MMX]
  #{3.1}
  [:void :half :float :double :X86-FP80 :FP128
   :PPC-FP128 :label :integer :function :struct
   :array :pointer :vector :metadata :X86-MMX])

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

(defenum verifier-failure-action
  [:abort-process :print-message :return-status])

(defpointers
  context-ref
  module-ref
  module-provider-ref
  type-ref
  value-ref
  pass-manager-ref
  execution-engine-ref
  generic-value-ref

  execution-engine-ref*
  value-ref*
  type-ref*
  char**)

(def ^:private llvm-api
  [;; Context
   [:ContextCreate [] :context-ref]
   [:GetGlobalContext [] :context-ref]
   [:ContextDispose [:context-ref] :void]
   [:GetMDKindIDInContext [:context-ref :constchar* :unsigned] :unsigned]
   [:GetMDKindID [:constchar* :unsigned] :unsigned]
   ;; Module
   [:ModuleCreateWithName [:constchar*] :module-ref]
   [:ModuleCreateWithNameInContext [:constchar* :context-ref] :module-ref]
   [:DisposeModule [:module-ref] :void]
   [:GetDataLayout [:module-ref] :constchar*]
   [:SetDataLayout [:module-ref :constchar*] :void]
   [:GetTarget [:module-ref] :constchar*]
   [:SetTarget [:module-ref :constchar*] :void]
   [:LinkModules [:module-ref :module-ref :bool :char**] :bool #{3.2}]
   [:DumpModule [:module-ref] :void]
   [:LLVMPrintModuleToFile [:module-ref :constchar* :char**] :bool #{3.2}]
   [:SetModuleInlineAsm [:module-ref :constchar*] :void]
   [:GetModuleContext [:module-ref] :context-ref]
   [:GetTypeByName [:module-ref :constchar*] :type-ref]
   [:GetNamedMetadataNumOperands [:module-ref :constchar*] :unsigned #{3.2}]
   [:GetNamedMetadataOperands [:module-ref :constchar* :value-ref*] :void #{3.2}]
   [:AddNamedMetadataOperands [:module-ref :constchar* :value-ref] :void #{3.2}]
   [:AddFunction [:module-ref :constchar* :type-ref] :value-ref]
   [:GetNamedFunction [:module-ref :constchar*] :value-ref]
   [:GetFirstFunction [:module-ref] :value-ref]
   [:GetLastFunction [:module-ref] :value-ref]
   [:GetNextFunction [:module-ref] :value-ref]
   [:GetPreviousFunction [:module-ref] :value-ref]
   [:AddGlobal [:module-ref :type-ref :constchar*] :value-ref]
   [:AddGlobalInAddressSpace [:module-ref :type-ref :constchar* :unsigned] :value-ref]
   [:GetNamedGlobal [:module-ref :constchar*] :value-ref]
   [:GetFirstGlobal [:module-ref] :value-ref]
   [:GetLastGlobal [:module-ref] :value-ref]
   [:GetNextGlobal [:value-ref] :value-ref]
   [:GetPreviousGlobal [:value-ref] :value-ref]
   [:AddAlias [:module-ref :type-ref :value-ref :constchar*] :value-ref]
   [:VerifyModule [:module-ref :verifier-failure-action]]
   ;; Types
   [:IntType [:unsigned] :type-ref]
   [:Int1Type [] :type-ref]
   [:Int8Type [] :type-ref]
   [:Int16Type [] :type-ref]
   [:Int32Type [] :type-ref]
   [:Int64Type [] :type-ref]
   [:IntTypeInContext [:context-ref :unsigned] :type-ref]
   [:Int1TypeInContext [:context-ref] :type-ref]
   [:Int8TypeInContext [:context-ref] :type-ref]
   [:Int16TypeInContext [:context-ref] :type-ref]
   [:Int32TypeInContext [:context-ref] :type-ref]
   [:Int64TypeInContext [:context-ref] :type-ref]
   [:GetIntTypeWidth [:type-ref] :unsigned]
   [:HalfType [] :type-ref #{3.1 3.2}]
   [:FloatType [] :type-ref]
   [:DoubleType [] :type-ref]
   [:X86FP80Type [] :type-ref]
   [:FP128Type [] :type-ref]
   [:PPCFP128Type [] :type-ref]
   [:HalfTypeInContext [:context-ref] :type-ref #{3.1 3.2}]
   [:FloatTypeInContext [:context-ref] :type-ref]
   [:DoubleTypeInContext [:context-ref] :type-ref]
   [:X86FP80TypeInContext [:context-ref] :type-ref]
   [:FP128TypeInContext [:context-ref] :type-ref]
   [:PPCFP128TypeInContext [:context-ref] :type-ref]
   [:FunctionType [:type-ref :type-ref* :unsigned :bool] :type-ref]
   [:CountParamTypes [:type-ref] :unsigned]
   [:GetParamTypes [:type-ref :type-ref*] :void]
   [:GetReturnType [:type-ref] :type-ref]
   [:IsFunctionVarArg [:type-ref] :bool]
   [:GetTypeKind [:type-ref] :type-kind]
   ;; PassManager
   [:CreatePassManager [] :pass-manager-ref]
   [:CreateFunctionPassManagerForModule [:module-ref] :pass-manager-ref]
   [:CreateFunctionPassManager [:module-provider-ref] :pass-manager-ref]
   [:FinalizeFunctionPassManager [:pass-manager-ref] :bool]
   [:DisposePassManager [:pass-manager-ref] :void]
   [:RunPassManager [:pass-manager-ref :module-ref] :bool]
   [:RunFunctionPassManager [:pass-manager-ref :value-ref] :bool]
   ;; Passes
   [:AddInternalizePass [:pass-manager-ref :unsigned] :void]
   [:AddAlwaysInlinerPass [:pass-manager-ref] :void]
   [:AddVerifierPass [:pass-manager-ref] :void]
   [:AddGlobalOptimizerPass [:pass-manager-ref] :void]
   [:AddIPSCCPPass [:pass-manager-ref] :void]
   [:AddDeadArgEliminationPass [:pass-manager-ref] :void]
   [:AddInstructionCombiningPass [:pass-manager-ref] :void]
   [:AddCFGSimplificationPass [:pass-manager-ref] :void]
   [:AddFunctionInliningPass [:pass-manager-ref] :void]
   [:AddFunctionAttrsPass [:pass-manager-ref] :void]
   [:AddScalarReplAggregatesPass [:pass-manager-ref] :void]
   [:AddScalarReplAggregatesPassSSA [:pass-manager-ref] :void]
   [:AddScalarReplAggregatesPassWithThreshold [:pass-manager-ref :int] :void]
   [:AddJumpThreadingPass [:pass-manager-ref] :void]
   [:AddIPConstantPropagationPass [:pass-manager-ref] :void]
   [:AddConstantPropagationPass [:pass-manager-ref] :void]
   [:AddReassociatePass [:pass-manager-ref] :void]
   [:AddLoopRotatePass [:pass-manager-ref] :void]
   [:AddLICMPass [:pass-manager-ref] :void]
   [:AddLoopUnswitchPass [:pass-manager-ref] :void]
   [:AddLoopDeletionPass [:pass-manager-ref] :void]
   [:AddLoopUnrollPass [:pass-manager-ref] :void]
   [:AddGVNPass [:pass-manager-ref] :void]
   [:AddMemCpyOptPass [:pass-manager-ref] :void]
   [:AddSCCPPass [:pass-manager-ref] :void]
   [:AddDeadStoreEliminationPass [:pass-manager-ref] :void]
   [:AddStripDeadPrototypesPass [:pass-manager-ref] :void]
   [:AddStripSymbolsPass [:pass-manager-ref] :void]
   [:AddConstantMergePass [:pass-manager-ref] :void]
   [:AddArgumentPromotionPass [:pass-manager-ref] :void]
   [:AddTailCallEliminationPass [:pass-manager-ref] :void]
   [:AddIndVarSimplifyPass [:pass-manager-ref] :void]
   [:AddAggressiveDCEPass [:pass-manager-ref] :void]
   [:AddGlobalDCEPass [:pass-manager-ref] :void]
   [:AddCorrelatedValuePropagationPass [:pass-manager-ref] :void]
   [:AddPruneEHPass [:pass-manager-ref] :void]
   [:AddSimplifyLibCallsPass [:pass-manager-ref] :void]
   [:AddLoopIdiomPass [:pass-manager-ref] :void]
   [:AddEarlyCSEPass [:pass-manager-ref] :void]
   [:AddTypeBasedAliasAnalysisPass [:pass-manager-ref] :void]
   [:AddBasicAliasAnalysisPass [:pass-manager-ref] :void]
   [:AddPromoteMemoryToRegisterPass [:pass-manager-ref] :void]
   [:AddDemoteMemoryToRegisterPass [:pass-manager-ref] :void]
   [:AddLowerExpectIntrinsicPass [:pass-manager-ref] :void]
   [:AddBBVectorizePass [:pass-manager-ref] :void #{3.1 3.2}]
   [:AddLoopVectorizePass [:pass-manager-ref] :void #{3.2}]
   ;;ExecutionEngine
   [:LinkInJIT [] :void]
   [:LinkInInterpreter [] :void]
   [:CreateExecutionEngine [:execution-engine-ref* :module-provider-ref :char**] :bool]
   [:CreateExecutionEngineForModule [:execution-engine-ref* :module-ref :char**] :bool]
   [:CreateInterpreter [:execution-engine-ref* :module-provider-ref :char**] :bool]
   [:CreateInterpreterForModule [:execution-engine-ref* :module-ref :char**] :bool]
   [:CreateJITCompiler [:execution-engine-ref* :module-provider-ref :unsigned :char**] :bool]
   [:CreateJITCompilerForModule [:execution-engine-ref* :module-ref :unsigned :char**] :bool]
   [:DisposeExecutionEngine [:execution-engine-ref] :void]
   [:RunStaticConstructors [:execution-engine-ref] :void]
   [:RunStaticDestructors [:execution-engine-ref] :void]
   ;;GenericValue
   [:CreateGenericValueOfInt [:type-ref :longlong :bool] :generic-value-ref]
   [:CreateGenericValueOfPointer [:void*] :generic-value-ref]
   [:CreateGenericValueOfFloat [:type-ref :double] :generic-value-ref]
   [:GenericValueIntWidth [:generic-value-ref] :unsigned]
   [:GenericValueToInt [:generic-value-ref :bool] :longlong]
   [:GenericValueToPointer [:generic-value-ref] :void*]
   [:GenericValueToFloat [:type-ref :generic-value-ref] :double]
   [:DisposeGenericValue [:generic-value-ref] :void]])
