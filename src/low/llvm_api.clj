(def-enum byte-ordering
  [:big-endian :little-endian])

(def-enum type-kind
  [:void :half :float :double :X86FP80 :FP128
   :PPC_FP128 :label :integer :function :struct
   :array :pointer :opaque :vector :metadata :X86_MMX])

(def-enum opcode
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

(def-enum linkage
  [:external :available-external :link-once-any
   :link-once-odr :weak-any :weak-odr :appending
   :internal :private :dll-import :dll-export
   :external-weak :ghost :common :linker-private
   :linker-private-weak :linker-private-weak-def-auto])

(def-enum visibility
  [:default :hidden :protected])

(def-enum call-conv
  {0 :C 8 :fast 9 :cold 64 :x86-std 65 :x86-fast})

(def-enum attribute
  (zipmap (concat (map (partial bit-shift-left 1) (range 16))
                  [2031616 2097152 4194304 8388608 16777216
                   33554432 469762048 536870912 1073741824 2147483648])
          [:z-ext :s-ext :no-return :in-reg :struct-ret
           :no-unwind :no-alias :by-val :nest :read-none
           :read-only :no-inline :always-inline
           :optimize-for-size :stack-protect
           :stack-protect-req :alignment :no-capture
           :no-red-zone :no-implicit-float :naked
           :inline-hint :stack-alignment :returns-twice
           :uw-table :non-lazy-bind]))

(def-enum int-predicate
  (zipmap (range 32 42) [:eq :ne :ugt :uge :ult
                         :ule :sgt :sge :slt :sle]))

(def-enum real-predicate
  [nil :oeq :ogt :oge :olt :ole :one :ord
   :uno :ueq :ugt :uge :ult :ule :une])

(def-enum atomic-bin-op
  [:xchg :add :sub :and :nand :or :xor
   :max :min :umax :umin])

(def-enum atomic-ordering
  [:no-atomic :unordered :monotonic :consume :acquire
   :release :acquire-release :sequentially-consistent])

(def-enum bool
  [false true])

(def-pointers
  ModuleRef
  OpaqueModule

  ContextRef
  OpaqueContext

  TypeRef
  OpaqueType

  ValueRef
  OpaqueValue

  BasicBlockRef
  OpaqueBasicBlock

  BuilderRef
  OpaqueBuilder

  MemoryBufferRef
  OpaqueMemoryBuffer

  PassManagerRef
  OpaquePassManager

  PassManagerBuilderRef
  OpaquePassManagerBuilder

  UseRef
  OpaqueUse

  TargetDataRef
  OpaqueTargetData

  ObjectFileRef
  OpaqueObjectFile

  SectionIteratorRef
  OpaqueSectionIterator

  StringPtr
  TypeRefPtr
  ValueRefPtr
  IntegerPtr
  BasicBlockRefPtr
  BytePtr)

(def ^:private llvm-api
  [[:ContextCreate [] ContextRef]
   [:GetGlobalContext [] ContextRef]
   [:ContextDispose [ContextRef] Void]
   [:GetMDKindIDInContext [ContextRef StringPtr Integer] Integer]
   [:GetMDKindID [StringPtr Integer] Integer]
   [:ModuleCreateWithNameInContext [StringPtr ContextRef] ModuleRef]
   [:DisposeModule [ModuleRef] Void]
   [:GetDataLayout [ModuleRef] StringPtr]
   [:SetDataLayout [ModuleRef StringPtr] Void]
   [:GetTarget [ModuleRef] StringPtr]
   [:SetTarget [ModuleRef StringPtr] Void]
   [:DumpModule [ModuleRef] Void]
   [:SetModuleInlineAsm [ModuleRef StringPtr] Void]
   [:GetTypeKind [TypeRef] type-kind]
   [:GetTypeContext [TypeRef] ContextRef]
   [:Int1TypeInContext [ContextRef] TypeRef]
   [:Int8TypeInContext [ContextRef] TypeRef]
   [:Int16TypeInContext [ContextRef] TypeRef]
   [:Int32TypeInContext [ContextRef] TypeRef]
   [:Int64TypeInContext [ContextRef] TypeRef]
   [:IntTypeInContext [ContextRef Integer] TypeRef]
   [:Int1Type [] TypeRef]
   [:Int8Type [] TypeRef]
   [:Int16Type [] TypeRef]
   [:Int32Type [] TypeRef]
   [:Int64Type [] TypeRef]
   [:IntType [Integer] TypeRef]
   [:GetIntTypeWidth [TypeRef] Integer]
   [:FloatTypeInContext [ContextRef] TypeRef]
   [:DoubleTypeInContext [ContextRef] TypeRef]
   [:X86FP80TypeInContext [ContextRef] TypeRef]
   [:FP128TypeInContext [ContextRef] TypeRef]
   [:PPCFP128TypeInContext [ContextRef] TypeRef]
   [:FloatType [] TypeRef]
   [:DoubleType [] TypeRef]
   [:X86FP80Type [] TypeRef]
   [:FP128Type [] TypeRef]
   [:PPCFP128Type [] TypeRef]
   [:FunctionType [TypeRef TypeRefPtr Integer bool] TypeRef]
   [:IsFunctionVarArg [TypeRef] bool]
   [:GetReturnType [TypeRef] TypeRef]
   [:CountParamTypes [TypeRef] Integer]
   [:GetParamTypes [TypeRef TypeRefPtr] Void]
   [:StructTypeInContext [ContextRef TypeRefPtr Integer bool] TypeRef]
   [:StructType [TypeRefPtr Integer bool] TypeRef]
   [:CountStructElementTypes [TypeRef] Integer]
   [:GetStructElementTypes [TypeRef TypeRefPtr] Void]
   [:IsPackedStruct [TypeRef] bool]
   [:ArrayType [TypeRef Integer] TypeRef]
   [:PointerType [TypeRef Integer] TypeRef]
   [:VectorType [TypeRef Integer] TypeRef]
   [:GetElementType [TypeRef] TypeRef]
   [:GetArrayLength [TypeRef] Integer]
   [:GetPointerAddressSpace [TypeRef] Integer]
   [:GetVectorSize [TypeRef] Integer]
   [:VoidTypeInContext [ContextRef] TypeRef]
   [:LabelTypeInContext [ContextRef] TypeRef]
   [:MetadataTypeInContext [ContextRef] TypeRef]
   [:VoidType [] TypeRef]
   [:LabelType [] TypeRef]
   [:MetadataType [] TypeRef]
   [:TypeOf [ValueRef] TypeRef]
   [:GetValueName [ValueRef] StringPtr]
   [:SetValueName [ValueRef StringPtr] Void]
   [:DumpValue [ValueRef] Void]
   [:ReplaceAllUsesWith [ValueRef ValueRef] Void]
   [:HasMetadata [ValueRef] Integer]
   [:GetMetadata [ValueRef Integer] ValueRef]
   [:SetMetadata [ValueRef Integer ValueRef] Void]
   [:GetFirstUse [ValueRef] UseRef]
   [:GetNextUse [UseRef] UseRef]
   [:GetUser [UseRef] ValueRef]
   [:GetUsedValue [UseRef] ValueRef]
   [:GetOperand [ValueRef Integer] ValueRef]
   [:SetOperand [ValueRef Integer ValueRef] Void]
   [:ConstNull [TypeRef] ValueRef]
   [:ConstAllOnes [TypeRef] ValueRef]
   [:GetUndef [TypeRef] ValueRef]
   [:IsConstant [ValueRef] bool]
   [:IsNull [ValueRef] bool]
   [:IsUndef [ValueRef] bool]
   [:ConstPointerNull [TypeRef] ValueRef]
   [:MDStringInContext [ContextRef StringPtr Integer] ValueRef]
   [:MDString [StringPtr Integer] ValueRef]
   [:MDNodeInContext [ContextRef ValueRefPtr Integer] ValueRef]
   [:MDNode [ValueRefPtr Integer] ValueRef]
   [:AddNamedMetadataOperand [ModuleRef StringPtr ValueRef] Void]
   [:ConstInt [TypeRef Long bool] ValueRef]
   [:ConstIntOfString [TypeRef StringPtr BytePtr] ValueRef]
   [:ConstIntOfStringAndSize [TypeRef StringPtr Integer BytePtr] ValueRef]
   [:ConstReal [TypeRef Double] ValueRef]
   [:ConstRealOfString [TypeRef StringPtr] ValueRef]
   [:ConstRealOfStringAndSize [TypeRef StringPtr Integer] ValueRef]
   [:ConstIntGetZExtValue [ValueRef] Long]
   [:ConstIntGetSExtValue [ValueRef] Long]
   [:ConstStringInContext [ContextRef StringPtr Integer bool] ValueRef]
   [:ConstStructInContext [ContextRef ValueRefPtr Integer bool] ValueRef]
   [:ConstString [StringPtr Integer bool] ValueRef]
   [:ConstArray [TypeRef ValueRefPtr Integer] ValueRef]
   [:ConstStruct [ValueRefPtr Integer bool] ValueRef]
   [:ConstVector [ValueRefPtr Integer] ValueRef]
   [:AlignOf [TypeRef] ValueRef]
   [:SizeOf [TypeRef] ValueRef]
   [:ConstNeg [ValueRef] ValueRef]
   [:ConstNSWNeg [ValueRef] ValueRef]
   [:ConstNUWNeg [ValueRef] ValueRef]
   [:ConstFNeg [ValueRef] ValueRef]
   [:ConstNot [ValueRef] ValueRef]
   [:ConstAdd [ValueRef ValueRef] ValueRef]
   [:ConstNSWAdd [ValueRef ValueRef] ValueRef]
   [:ConstNUWAdd [ValueRef ValueRef] ValueRef]
   [:ConstFAdd [ValueRef ValueRef] ValueRef]
   [:ConstSub [ValueRef ValueRef] ValueRef]
   [:ConstNSWSub [ValueRef ValueRef] ValueRef]
   [:ConstNUWSub [ValueRef ValueRef] ValueRef]
   [:ConstFSub [ValueRef ValueRef] ValueRef]
   [:ConstMul [ValueRef ValueRef] ValueRef]
   [:ConstNSWMul [ValueRef ValueRef] ValueRef]
   [:ConstNUWMul [ValueRef ValueRef] ValueRef]
   [:ConstFMul [ValueRef ValueRef] ValueRef]
   [:ConstUDiv [ValueRef ValueRef] ValueRef]
   [:ConstSDiv [ValueRef ValueRef] ValueRef]
   [:ConstExactSDiv [ValueRef ValueRef] ValueRef]
   [:ConstFDiv [ValueRef ValueRef] ValueRef]
   [:ConstURem [ValueRef ValueRef] ValueRef]
   [:ConstSRem [ValueRef ValueRef] ValueRef]
   [:ConstFRem [ValueRef ValueRef] ValueRef]
   [:ConstAnd [ValueRef ValueRef] ValueRef]
   [:ConstOr [ValueRef ValueRef] ValueRef]
   [:ConstXor [ValueRef ValueRef] ValueRef]
   [:ConstShl [ValueRef ValueRef] ValueRef]
   [:ConstLShr [ValueRef ValueRef] ValueRef]
   [:ConstAShr [ValueRef ValueRef] ValueRef]
   [:ConstGEP [ValueRef IntegerPtr Integer] ValueRef]
   [:ConstInBoundsGEP [ValueRef IntegerPtr Integer] ValueRef]
   [:ConstTrunc [ValueRef TypeRef] ValueRef]
   [:ConstSExt [ValueRef TypeRef] ValueRef]
   [:ConstZExt [ValueRef TypeRef] ValueRef]
   [:ConstFPTrunc [ValueRef TypeRef] ValueRef]
   [:ConstFPExt [ValueRef TypeRef] ValueRef]
   [:ConstUIToFP [ValueRef TypeRef] ValueRef]
   [:ConstSIToFP [ValueRef TypeRef] ValueRef]
   [:ConstFPToUI [ValueRef TypeRef] ValueRef]
   [:ConstFPToSI [ValueRef TypeRef] ValueRef]
   [:ConstPtrToInt [ValueRef TypeRef] ValueRef]
   [:ConstIntToPtr [ValueRef TypeRef] ValueRef]
   [:ConstBitCast [ValueRef TypeRef] ValueRef]
   [:ConstZExtOrBitCast [ValueRef TypeRef] ValueRef]
   [:ConstSExtOrBitCast [ValueRef TypeRef] ValueRef]
   [:ConstTruncOrBitCast [ValueRef TypeRef] ValueRef]
   [:ConstPointerCast [ValueRef TypeRef] ValueRef]
   [:ConstIntCast [ValueRef TypeRef bool] ValueRef]
   [:ConstFPCast [ValueRef TypeRef] ValueRef]
   [:ConstSelect [ValueRef ValueRef ValueRef] ValueRef]
   [:ConstExtractElement [ValueRef ValueRef] ValueRef]
   [:ConstInsertElement [ValueRef ValueRef ValueRef] ValueRef]
   [:ConstShuffleVector [ValueRef ValueRef ValueRef] ValueRef]
   [:ConstExtractValue [ValueRef IntegerPtr Integer] ValueRef]
   [:ConstInsertValue [ValueRef ValueRef IntegerPtr Integer] ValueRef]
   [:ConstInlineAsm [TypeRef StringPtr StringPtr bool bool] ValueRef]
   [:BlockAddress [ValueRef BasicBlockRef] ValueRef]
   [:GetGlobalParent [ValueRef] ModuleRef]
   [:IsDeclaration [ValueRef] bool]
   [:GetLinkage [ValueRef] linkage]
   [:SetLinkage [ValueRef linkage] Void]
   [:GetSection [ValueRef] StringPtr]
   [:SetSection [ValueRef StringPtr] Void]
   [:GetVisibility [ValueRef] visibility]
   [:SetVisibility [ValueRef visibility] Void]
   [:GetAlignment [ValueRef] Integer]
   [:SetAlignment [ValueRef Integer] Void]
   [:AddGlobal [ModuleRef TypeRef StringPtr] ValueRef]
   [:AddGlobalInAddressSpace [ModuleRef TypeRef StringPtr Integer] ValueRef]
   [:GetNamedGlobal [ModuleRef StringPtr] ValueRef]
   [:GetFirstGlobal [ModuleRef] ValueRef]
   [:GetLastGlobal [ModuleRef] ValueRef]
   [:GetNextGlobal [ValueRef] ValueRef]
   [:GetPreviousGlobal [ValueRef] ValueRef]
   [:DeleteGlobal [ValueRef] Void]
   [:GetInitializer [ValueRef] ValueRef]
   [:SetInitializer [ValueRef ValueRef] Void]
   [:IsThreadLocal [ValueRef] bool]
   [:SetThreadLocal [ValueRef bool] Void]
   [:IsGlobalConstant [ValueRef] bool]
   [:SetGlobalConstant [ValueRef bool] Void]
   [:AddAlias [ModuleRef TypeRef ValueRef StringPtr] ValueRef]
   [:AddFunction [ModuleRef StringPtr TypeRef] ValueRef]
   [:GetNamedFunction [ModuleRef StringPtr] ValueRef]
   [:GetFirstFunction [ModuleRef] ValueRef]
   [:GetLastFunction [ModuleRef] ValueRef]
   [:GetNextFunction [ValueRef] ValueRef]
   [:GetPreviousFunction [ValueRef] ValueRef]
   [:DeleteFunction [ValueRef] Void]
   [:GetOrInsertFunction [ModuleRef StringPtr TypeRef] ValueRef]
   [:GetIntrinsicID [ValueRef] Integer]
   [:GetFunctionCallConv [ValueRef] call-conv]
   [:SetFunctionCallConv [ValueRef call-conv] Void]
   [:GetGC [ValueRef] StringPtr]
   [:SetGC [ValueRef StringPtr] Void]
   [:AddFunctionAttr [ValueRef attribute] Void]
   [:GetFunctionAttr [ValueRef] attribute]
   [:RemoveFunctionAttr [ValueRef Long Long] Void]
   [:CountParams [ValueRef] Integer]
   [:GetParams [ValueRef ValueRefPtr] Void]
   [:GetParam [ValueRef Integer] ValueRef]
   [:GetParamParent [ValueRef] ValueRef]
   [:GetFirstParam [ValueRef] ValueRef]
   [:GetLastParam [ValueRef] ValueRef]
   [:GetNextParam [ValueRef] ValueRef]
   [:GetPreviousParam [ValueRef] ValueRef]
   [:AddAttribute [ValueRef Integer] Void]
   [:RemoveAttribute [ValueRef Integer] Void]
   [:GetAttribute [ValueRef] Integer]
   [:SetParamAlignment [ValueRef Integer] Void]
   [:BasicBlockAsValue [BasicBlockRef] ValueRef]
   [:ValueIsBasicBlock [ValueRef] bool]
   [:ValueAsBasicBlock [ValueRef] BasicBlockRef]
   [:GetBasicBlockParent [BasicBlockRef] ValueRef]
   [:CountBasicBlocks [ValueRef] Integer]
   [:GetBasicBlocks [ValueRef ValueRefPtr] Void]
   [:GetFirstBasicBlock [ValueRef] BasicBlockRef]
   [:GetLastBasicBlock [ValueRef] BasicBlockRef]
   [:GetNextBasicBlock [BasicBlockRef] BasicBlockRef]
   [:GetPreviousBasicBlock [BasicBlockRef] BasicBlockRef]
   [:GetEntryBasicBlock [ValueRef] BasicBlockRef]
   [:AppendBasicBlockInContext [ContextRef ValueRef StringPtr] BasicBlockRef]
   [:InsertBasicBlockInContext [ContextRef BasicBlockRef StringPtr] BasicBlockRef]
   [:AppendBasicBlock [ValueRef StringPtr] BasicBlockRef]
   [:InsertBasicBlock [BasicBlockRef StringPtr] BasicBlockRef]
   [:DeleteBasicBlock [BasicBlockRef] Void]
   [:GetInstructionParent [ValueRef] BasicBlockRef]
   [:GetFirstInstruction [BasicBlockRef] ValueRef]
   [:GetLastInstruction [BasicBlockRef] ValueRef]
   [:GetNextInstruction [ValueRef] ValueRef]
   [:GetPreviousInstruction [ValueRef] ValueRef]
   [:SetInstructionCallConv [ValueRef Integer] Void]
   [:GetInstructionCallConv [ValueRef] Integer]
   [:AddInstrAttribute [ValueRef Integer Integer] Void]
   [:RemoveInstrAttribute [ValueRef Integer Integer] Void]
   [:SetInstrParamAlignment [ValueRef Integer Integer] Void]
   [:IsTailCall [ValueRef] bool]
   [:SetTailCall [ValueRef bool] Void]
   [:AddIncoming [ValueRef ValueRefPtr BasicBlockRefPtr Integer] Void]
   [:CountIncoming [ValueRef] Integer]
   [:GetIncomingValue [ValueRef Integer] ValueRef]
   [:GetIncomingBlock [ValueRef Integer] BasicBlockRef]
   [:CreateBuilderInContext [ContextRef] BuilderRef]
   [:CreateBuilder [] BuilderRef]
   [:PositionBuilder [BuilderRef BasicBlockRef ValueRef] Void]
   [:PositionBuilderBefore [BuilderRef ValueRef] Void]
   [:PositionBuilderAtEnd [BuilderRef BasicBlockRef] Void]
   [:GetInsertBlock [BuilderRef] BasicBlockRef]
   [:ClearInsertionPosition [BuilderRef] Void]
   [:InsertIntoBuilder [BuilderRef ValueRef] Void]
   [:InsertIntoBuilderWithName [BuilderRef ValueRef StringPtr] Void]
   [:DisposeBuilder [BuilderRef] Void]
   [:SetCurrentDebugLocation [BuilderRef ValueRef] Void]
   [:GetCurrentDebugLocation [BuilderRef] ValueRef]
   [:SetInstDebugLocation [BuilderRef ValueRef] Void]
   [:BuildRetVoid [BuilderRef] ValueRef]
   [:BuildRet [BuilderRef ValueRef] ValueRef]
   [:BuildAggregateRet [BuilderRef ValueRefPtr Integer] ValueRef]
   [:BuildBr [BuilderRef BasicBlockRef] ValueRef]
   [:BuildCondBr [BuilderRef ValueRef BasicBlockRef BasicBlockRef] ValueRef]
   [:BuildSwitch [BuilderRef ValueRef BasicBlockRef Integer] ValueRef]
   [:BuildIndirectBr [BuilderRef ValueRef Integer] ValueRef]
   [:BuildInvoke [BuilderRef ValueRef ValueRefPtr Integer BasicBlockRef BasicBlockRef StringPtr] ValueRef]
   [:BuildLandingPad [BuilderRef TypeRef ValueRef Integer StringPtr] ValueRef]
   [:BuildResume [BuilderRef ValueRef] ValueRef]
   [:BuildUnreachable [BuilderRef] ValueRef]
   [:AddCase [ValueRef ValueRef BasicBlockRef] Void]
   [:AddDestination [ValueRef BasicBlockRef] Void]
   [:AddClause [ValueRef ValueRef] Void]
   [:SetCleanup [ValueRef bool] Void]
   [:BuildAdd [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNSWAdd [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNUWAdd [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFAdd [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildSub [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNSWSub [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNUWSub [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFSub [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildMul [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNSWMul [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNUWMul [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFMul [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildUDiv [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildSDiv [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildExactSDiv [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFDiv [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildURem [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildSRem [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFRem [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildShl [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildLShr [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildAShr [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildAnd [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildOr [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildXor [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildBinOp [BuilderRef opcode ValueRef ValueRef StringPtr] ValueRef]
   [:BuildNeg [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildNSWNeg [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildNUWNeg [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildFNeg [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildNot [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildMalloc [BuilderRef TypeRef StringPtr] ValueRef]
   [:BuildArrayMalloc [BuilderRef TypeRef ValueRef StringPtr] ValueRef]
   [:BuildAlloca [BuilderRef TypeRef StringPtr] ValueRef]
   [:BuildArrayAlloca [BuilderRef TypeRef ValueRef StringPtr] ValueRef]
   [:BuildFree [BuilderRef ValueRef] ValueRef]
   [:BuildLoad [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildStore [BuilderRef ValueRef ValueRef] ValueRef]
   [:BuildGEP [BuilderRef ValueRef ValueRefPtr Integer StringPtr] ValueRef]
   [:BuildInBoundsGEP [BuilderRef ValueRef ValueRefPtr Integer StringPtr] ValueRef]
   [:BuildStructGEP [BuilderRef ValueRef Integer StringPtr] ValueRef]
   [:BuildGlobalString [BuilderRef StringPtr StringPtr] ValueRef]
   [:BuildGlobalStringPtr [BuilderRef StringPtr StringPtr] ValueRef]
   [:BuildTrunc [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildZExt [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildSExt [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildFPToUI [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildFPToSI [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildUIToFP [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildSIToFP [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildFPTrunc [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildFPExt [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildPtrToInt [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildIntToPtr [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildBitCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildZExtOrBitCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildSExtOrBitCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildTruncOrBitCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildCast [BuilderRef opcode ValueRef TypeRef StringPtr] ValueRef]
   [:BuildPointerCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildIntCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildFPCast [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildICmp [BuilderRef Integer ValueRef ValueRef StringPtr] ValueRef]
   [:BuildFCmp [BuilderRef Integer ValueRef ValueRef StringPtr] ValueRef]
   [:BuildPhi [BuilderRef TypeRef StringPtr] ValueRef]
   [:BuildCall [BuilderRef ValueRef ValueRefPtr Integer StringPtr] ValueRef]
   [:BuildSelect [BuilderRef ValueRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildVAArg [BuilderRef ValueRef TypeRef StringPtr] ValueRef]
   [:BuildExtractElement [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildInsertElement [BuilderRef ValueRef ValueRef ValueRef] StringPtr ValueRef]
   [:BuildShuffleVector [BuilderRef ValueRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildExtractValue [BuilderRef ValueRef Integer StringPtr] ValueRef]
   [:BuildInsertValue [BuilderRef ValueRef ValueRef Integer StringPtr] ValueRef]
   [:BuildIsNull [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildIsNotNull [BuilderRef ValueRef StringPtr] ValueRef]
   [:BuildPtrDiff [BuilderRef ValueRef ValueRef StringPtr] ValueRef]
   [:BuildAtomicRMW [BuilderRef atomic-bin-op ValueRef ValueRef atomic-ordering] ValueRef]
   [:IsATerminatorInst [ValueRef] ValueRef]
   [:WriteBitcodeToFile [ModuleRef StringPtr] Integer]
   [:CreateTargetData [StringPtr] TargetDataRef]
   [:AddTargetData [TargetDataRef PassManagerRef] Void]
   [:StoreSizeOfType [TargetDataRef TypeRef] Long]
   [:SizeOfTypeInBits [TargetDataRef TypeRef] Long]
   [:ABISizeOfType [TargetDataRef TypeRef] Integer]
   [:PreferredAlignmentOfType [TargetDataRef TypeRef] Integer]
   [:ABIAlignmentOfType [TargetDataRef TypeRef] Integer]
   [:CallFrameAlignmentOfType [TargetDataRef TypeRef] Integer]
   [:DisposeTargetData [TargetDataRef] Void]
   [:CreatePassManager [] PassManagerRef]
   [:DisposePassManager [PassManagerRef] Void]
   [:RunPassManager [PassManagerRef ModuleRef] bool]
   [:AddVerifierPass [PassManagerRef] Void]
   [:AddGlobalOptimizerPass [PassManagerRef] Void]
   [:AddIPSCCPPass [PassManagerRef] Void]
   [:AddDeadArgEliminationPass [PassManagerRef] Void]
   [:AddInstructionCombiningPass [PassManagerRef] Void]
   [:AddCFGSimplificationPass [PassManagerRef] Void]
   [:AddFunctionInliningPass [PassManagerRef] Void]
   [:AddFunctionAttrsPass [PassManagerRef] Void]
   [:AddScalarReplAggregatesPass [PassManagerRef] Void]
   [:AddScalarReplAggregatesPassSSA [PassManagerRef] Void]
   [:AddJumpThreadingPass [PassManagerRef] Void]
   [:AddConstantPropagationPass [PassManagerRef] Void]
   [:AddReassociatePass [PassManagerRef] Void]
   [:AddLoopRotatePass [PassManagerRef] Void]
   [:AddLICMPass [PassManagerRef] Void]
   [:AddLoopUnswitchPass [PassManagerRef] Void]
   [:AddLoopDeletionPass [PassManagerRef] Void]
   [:AddLoopUnrollPass [PassManagerRef] Void]
   [:AddGVNPass [PassManagerRef] Void]
   [:AddMemCpyOptPass [PassManagerRef] Void]
   [:AddSCCPPass [PassManagerRef] Void]
   [:AddDeadStoreEliminationPass [PassManagerRef] Void]
   [:AddStripDeadPrototypesPass [PassManagerRef] Void]
   [:AddConstantMergePass [PassManagerRef] Void]
   [:AddArgumentPromotionPass [PassManagerRef] Void]
   [:AddTailCallEliminationPass [PassManagerRef] Void]
   [:AddIndVarSimplifyPass [PassManagerRef] Void]
   [:AddAggressiveDCEPass [PassManagerRef] Void]
   [:AddGlobalDCEPass [PassManagerRef] Void]
   [:AddCorrelatedValuePropagationPass [PassManagerRef] Void]
   [:AddPruneEHPass [PassManagerRef] Void]
   [:AddSimplifyLibCallsPass [PassManagerRef] Void]
   [:AddLoopIdiomPass [PassManagerRef] Void]
   [:AddEarlyCSEPass [PassManagerRef] Void]
   [:AddTypeBasedAliasAnalysisPass [PassManagerRef] Void]
   [:AddBasicAliasAnalysisPass [PassManagerRef] Void]
   [:PassManagerBuilderCreate [] PassManagerBuilderRef]
   [:PassManagerBuilderDispose [PassManagerBuilderRef] Void]
   [:PassManagerBuilderSetOptLevel [PassManagerBuilderRef Integer] Void]
   [:PassManagerBuilderSetSizeLevel [PassManagerBuilderRef bool] Void]
   [:PassManagerBuilderSetDisableUnitAtATime [PassManagerBuilderRef bool] Void]
   [:PassManagerBuilderSetDisableUnrollLoops [PassManagerBuilderRef bool] Void]
   [:PassManagerBuilderSetDisableSimplifyLibCalls [PassManagerBuilderRef bool] Void]
   [:PassManagerBuilderUseInlinerWithThreshold [PassManagerBuilderRef Integer] Void]
   [:PassManagerBuilderPopulateModulePassManager [PassManagerBuilderRef PassManagerRef] Void]
   [:PassManagerBuilderPopulateFunctionPassManager [PassManagerBuilderRef PassManagerRef] Void]
   [:DisposeMemoryBuffer [MemoryBufferRef] Void]
   [:CreateObjectFile [MemoryBufferRef] ObjectFileRef]
   [:DisposeObjectFile [ObjectFileRef] Void]
   [:GetSections [ObjectFileRef] SectionIteratorRef]
   [:DisposeSectionIterator [SectionIteratorRef] Void]
   [:IsSectionIteratorAtEnd [ObjectFileRef SectionIteratorRef] bool]
   [:MoveToNextSection [SectionIteratorRef] Void]
   [:GetSectionName [SectionIteratorRef] StringPtr]
   [:GetSectionSize [SectionIteratorRef] Long]
   [:GetSectionContents [SectionIteratorRef] StringPtr]
   [:StructCreateNamed [ContextRef StringPtr] TypeRef]
   [:StructSetBody [TypeRef TypeRefPtr Integer bool] Void]
   [:ConstNamedStruct [TypeRef ValueRefPtr Integer] ValueRef]
   [:LinkModules [ModuleRef ModuleRef] bool]])
