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

(defenum address-space
  [:generic :global :const-not-gen :shared :const :local])

(defpointers
  context
  module
  module-provider
  type
  value
  pass-manager
  execution-engine
  generic-value

  execution-engine*
  value*
  type*
  char**)

(def ^:private llvm-api
  [;; Context
   [:ContextCreate [] :context]
   [:GetGlobalContext [] :context]
   [:ContextDispose [:context] :void]
   [:GetMDKindIDInContext [:context :constchar* :unsigned] :unsigned]
   [:GetMDKindID [:constchar* :unsigned] :unsigned]
   ;; Module
   [:ModuleCreateWithName [:constchar*] :module]
   [:ModuleCreateWithNameInContext [:constchar* :context] :module]
   [:DisposeModule [:module] :void]
   [:GetDataLayout [:module] :constchar*]
   [:SetDataLayout [:module :constchar*] :void]
   [:GetTarget [:module] :constchar*]
   [:SetTarget [:module :constchar*] :void]
   [:LinkModules [:module :module :bool :char**] :bool #{3.2}]
   [:DumpModule [:module] :void]
   [:LLVMPrintModuleToFile [:module :constchar* :char**] :bool #{3.2}]
   [:SetModuleInlineAsm [:module :constchar*] :void]
   [:GetModuleContext [:module] :context]
   [:GetTypeByName [:module :constchar*] :type]
   [:GetNamedMetadataNumOperands [:module :constchar*] :unsigned #{3.2}]
   [:GetNamedMetadataOperands [:module :constchar* :value*] :void #{3.2}]
   [:AddNamedMetadataOperands [:module :constchar* :value] :void #{3.2}]
   [:AddFunction [:module :constchar* :type] :value]
   [:GetNamedFunction [:module :constchar*] :value]
   [:GetFirstFunction [:module] :value]
   [:GetLastFunction [:module] :value]
   [:GetNextFunction [:module] :value]
   [:GetPreviousFunction [:module] :value]
   [:AddGlobal [:module :type :constchar*] :value]
   [:AddGlobalInAddressSpace [:module :type :constchar* :unsigned] :value]
   [:GetNamedGlobal [:module :constchar*] :value]
   [:GetFirstGlobal [:module] :value]
   [:GetLastGlobal [:module] :value]
   [:GetNextGlobal [:value] :value]
   [:GetPreviousGlobal [:value] :value]
   [:AddAlias [:module :type :value :constchar*] :value]
   [:VerifyModule [:module :verifier-failure-action]]
   ;; Types
   [:GetTypeKind [:type] :type-kind]
   [:TypeIsSized [:type] :bool]
   [:GetTypeInContext [:type] :context]
   [:IntType [:unsigned] :type]
   [:Int1Type [] :type]
   [:Int8Type [] :type]
   [:Int16Type [] :type]
   [:Int32Type [] :type]
   [:Int64Type [] :type]
   [:IntTypeInContext [:context :unsigned] :type]
   [:Int1TypeInContext [:context] :type]
   [:Int8TypeInContext [:context] :type]
   [:Int16TypeInContext [:context] :type]
   [:Int32TypeInContext [:context] :type]
   [:Int64TypeInContext [:context] :type]
   [:GetIntTypeWidth [:type] :unsigned]
   [:HalfType [] :type #{3.1 3.2}]
   [:FloatType [] :type]
   [:DoubleType [] :type]
   [:X86FP80Type [] :type]
   [:FP128Type [] :type]
   [:PPCFP128Type [] :type]
   [:HalfTypeInContext [:context] :type #{3.1 3.2}]
   [:FloatTypeInContext [:context] :type]
   [:DoubleTypeInContext [:context] :type]
   [:X86FP80TypeInContext [:context] :type]
   [:FP128TypeInContext [:context] :type]
   [:PPCFP128TypeInContext [:context] :type]
   [:FunctionType [:type :type* :unsigned :bool] :type]
   [:CountParamTypes [:type] :unsigned]
   [:GetParamTypes [:type :type*] :void]
   [:GetReturnType [:type] :type]
   [:IsFunctionVarArg [:type] :bool]
   [:StructType [:type* :unsigned :bool] :type]
   [:StructTypeInContext [:context :type* :unsigned :bool] :type]
   [:StructCreateNamed [:context :char*] :type]
   [:GetStructName [:type] :constchar*]
   [:StructSetBody [:type :type* :unsigned :bool] :void]
   [:CountStructElementTypes [:type] :unsigned]
   [:GetStructElementTypes [:type :type*] :void]
   [:IsPackedStruct [:type] :bool]
   [:IsOpaqueStruct [:type] :bool]
   [:GetElementType [:type] :type]
   [:ArrayType [:type :unsigned] :type]
   [:GetArrayLength [:type] :unsigned]
   [:PointerType [:type :address-space] :type]
   [:GetPointerAddressSpace [:type] :address-space]
   [:VectorType [:type :unsigned] :type]
   [:GetVectorSize [:type] :unsigned]
   [:VoidType [] :type]
   [:VoidTypeInContext [:context] :type]
   [:LabelType [] :type]
   [:LabelTypeInContext [:context] :type]
   [:X86MMXType [] :type]
   [:X86MMXTypeInContext [:context] :type]
   ;; PassManager
   [:CreatePassManager [] :pass-manager]
   [:CreateFunctionPassManagerForModule [:module] :pass-manager]
   [:CreateFunctionPassManager [:module-provider] :pass-manager]
   [:FinalizeFunctionPassManager [:pass-manager] :bool]
   [:DisposePassManager [:pass-manager] :void]
   [:RunPassManager [:pass-manager :module] :bool]
   [:RunFunctionPassManager [:pass-manager :value] :bool]
   ;; Passes
   [:AddInternalizePass [:pass-manager :unsigned] :void]
   [:AddAlwaysInlinerPass [:pass-manager] :void]
   [:AddVerifierPass [:pass-manager] :void]
   [:AddGlobalOptimizerPass [:pass-manager] :void]
   [:AddIPSCCPPass [:pass-manager] :void]
   [:AddDeadArgEliminationPass [:pass-manager] :void]
   [:AddInstructionCombiningPass [:pass-manager] :void]
   [:AddCFGSimplificationPass [:pass-manager] :void]
   [:AddFunctionInliningPass [:pass-manager] :void]
   [:AddFunctionAttrsPass [:pass-manager] :void]
   [:AddScalarReplAggregatesPass [:pass-manager] :void]
   [:AddScalarReplAggregatesPassSSA [:pass-manager] :void]
   [:AddScalarReplAggregatesPassWithThreshold [:pass-manager :int] :void]
   [:AddJumpThreadingPass [:pass-manager] :void]
   [:AddIPConstantPropagationPass [:pass-manager] :void]
   [:AddConstantPropagationPass [:pass-manager] :void]
   [:AddReassociatePass [:pass-manager] :void]
   [:AddLoopRotatePass [:pass-manager] :void]
   [:AddLICMPass [:pass-manager] :void]
   [:AddLoopUnswitchPass [:pass-manager] :void]
   [:AddLoopDeletionPass [:pass-manager] :void]
   [:AddLoopUnrollPass [:pass-manager] :void]
   [:AddGVNPass [:pass-manager] :void]
   [:AddMemCpyOptPass [:pass-manager] :void]
   [:AddSCCPPass [:pass-manager] :void]
   [:AddDeadStoreEliminationPass [:pass-manager] :void]
   [:AddStripDeadPrototypesPass [:pass-manager] :void]
   [:AddStripSymbolsPass [:pass-manager] :void]
   [:AddConstantMergePass [:pass-manager] :void]
   [:AddArgumentPromotionPass [:pass-manager] :void]
   [:AddTailCallEliminationPass [:pass-manager] :void]
   [:AddIndVarSimplifyPass [:pass-manager] :void]
   [:AddAggressiveDCEPass [:pass-manager] :void]
   [:AddGlobalDCEPass [:pass-manager] :void]
   [:AddCorrelatedValuePropagationPass [:pass-manager] :void]
   [:AddPruneEHPass [:pass-manager] :void]
   [:AddSimplifyLibCallsPass [:pass-manager] :void]
   [:AddLoopIdiomPass [:pass-manager] :void]
   [:AddEarlyCSEPass [:pass-manager] :void]
   [:AddTypeBasedAliasAnalysisPass [:pass-manager] :void]
   [:AddBasicAliasAnalysisPass [:pass-manager] :void]
   [:AddPromoteMemoryToRegisterPass [:pass-manager] :void]
   [:AddDemoteMemoryToRegisterPass [:pass-manager] :void]
   [:AddLowerExpectIntrinsicPass [:pass-manager] :void]
   [:AddBBVectorizePass [:pass-manager] :void #{3.1 3.2}]
   [:AddLoopVectorizePass [:pass-manager] :void #{3.2}]
   ;;ExecutionEngine
   [:LinkInJIT [] :void]
   [:LinkInInterpreter [] :void]
   [:CreateExecutionEngine [:execution-engine* :module-provider :char**] :bool]
   [:CreateExecutionEngineForModule [:execution-engine* :module :char**] :bool]
   [:CreateInterpreter [:execution-engine* :module-provider :char**] :bool]
   [:CreateInterpreterForModule [:execution-engine* :module :char**] :bool]
   [:CreateJITCompiler [:execution-engine* :module-provider :unsigned :char**] :bool]
   [:CreateJITCompilerForModule [:execution-engine* :module :unsigned :char**] :bool]
   [:DisposeExecutionEngine [:execution-engine] :void]
   [:RunStaticConstructors [:execution-engine] :void]
   [:RunStaticDestructors [:execution-engine] :void]
   ;;GenericValue
   [:CreateGenericValueOfInt [:type :longlong :bool] :generic-value]
   [:CreateGenericValueOfPointer [:void*] :generic-value]
   [:CreateGenericValueOfFloat [:type :double] :generic-value]
   [:GenericValueIntWidth [:generic-value] :unsigned]
   [:GenericValueToInt [:generic-value :bool] :longlong]
   [:GenericValueToPointer [:generic-value] :void*]
   [:GenericValueToFloat [:type :generic-value] :double]
   [:DisposeGenericValue [:generic-value] :void]])
