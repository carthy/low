(def-enum byte-ordering
  #{3.0 3.1}
  [:big-endian :little-endian])

(def-enum type-kind
  #{3.1}
  [:void :half :float :double :X86FP80 :FP128
   :PPC_FP128 :label :integer :function :struct
   :array :pointer :opaque :vector :metadata :X86_MMX])

(def-enum opcode
  #{3.1}
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
  #{3.1}
  [:external :available-external :link-once-any
   :link-once-odr :weak-any :weak-odr :appending
   :internal :private :dll-import :dll-export
   :external-weak :ghost :common :linker-private
   :linker-private-weak :linker-private-weak-def-auto])

(def-enum visibility
  #{3.0 3.1}
  [:default :hidden :protected])

(def-enum call-conv
  #{3.0 3.1}
  {0 :C 8 :fast 9 :cold 64 :x86-std 65 :x86-fast})

(def-enum attribute
  #{3.1}
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
  #{3.0 3.1}
  (zipmap (range 32 42) [:eq :ne :ugt :uge :ult
                         :ule :sgt :sge :slt :sle]))

(def-enum real-predicate
  #{3.0 3.1}
  [nil :oeq :ogt :oge :olt :ole :one :ord
   :uno :ueq :ugt :uge :ult :ule :une])

(def-enum atomic-bin-op
  #{3.1}
  [:xchg :add :sub :and :nand :or :xor
   :max :min :umax :umin])

(def-enum atomic-ordering
  #{3.1}
  [:no-atomic :unordered :monotonic :consume :acquire
   :release :acquire-release :sequentially-consistent])

(def-enum bool
  #{3.0 3.1}
  [false true])

(def-pointers
  ModuleRef
  ContextRef
  TypeRef
  ValueRef
  BasicBlockRef
  BuilderRef
  MemoryBufferRef
  PassManagerRef
  PassManagerBuilderRef
  UseRef
  TargetDataRef
  ObjectFileRef
  SectionIteratorRef

  TypeRefPtr
  ValueRefPtr
  IntegerPtr
  BasicBlockRefPtr
  BytePtr)

(def ^:private llvm-api
  [;; Context
   [:ContextCreate [] ContextRef #{3.0 3.1}]
   [:GetGlobalContext [] ContextRef #{3.0 3.1}]
   [:ContextDispose [ContextRef] Void #{3.0 3.1}]
   [:GetMDKindIDInContext [ContextRef String Integer] Integer #{3.0 3.1}]
   [:GetMDKindID [String Integer] Integer #{3.0 3.1}]
   ;; Module
   [:ModuleCreateWithNameInContext [String ContextRef] ModuleRef #{3.0 3.1}]
   [:ModuleCreateWithName [String] ModuleRef #{3.0 3.1}]
   [:DisposeModule [ModuleRef] Void #{3.0 3.1}]
   [:GetDataLayout [ModuleRef] String #{3.0 3.1}]
   [:SetDataLayout [ModuleRef String] Void #{3.0 3.1}]
   [:GetTarget [ModuleRef] String #{3.0 3.1}]
   [:SetTarget [ModuleRef String] Void #{3.0 3.1}]
   [:DumpModule [ModuleRef] Void #{3.0 3.1}]
   #_[:LLVMPrintModuleToFile [ModuleRef String StringPtr] bool #{3.0 3.1}]
   [:SetModuleInlineAsm [ModuleRef String] Void #{3.0 3.1}]
   [:GetModuleContext [ModuleRef] ContextRef #{3.0 3.1}]
   [:GetTypeByName [ModuleRef String] TypeRef #{3.0 3.1}]
   #_[:GetNamedMetadataNumOperands [ModuleRef String] Integer]
   #_[:GetNamedMetadataOperands [ModuleRef String ValueRefPtr] Void]
   [:AddFunction [ModuleRef String TypeRef] ValueRef #{3.0 3.1}]
   [:GetNamedFunction [ModuleRef String] ValueRef #{3.0 3.1}]
   [:GetFirstFunction [ModuleRef] ValueRef #{3.0 3.1}]
   [:GetLastFunction [ModuleRef] ValueRef #{3.0 3.1}]
   [:AddGlobal [ModuleRef TypeRef String] ValueRef #{3.0 3.1}]
   [:AddGlobalInAddressSpace [ModuleRef TypeRef String Integer] ValueRef #{3.0 3.1}]
   [:GetNamedGlobal [ModuleRef String] ValueRef #{3.0 3.1}]
   [:GetFirstGlobal [ModuleRef] ValueRef #{3.0 3.1}]
   [:GetLastGlobal [ModuleRef] ValueRef #{3.0 3.1}]
   [:AddAlias [ModuleRef TypeRef ValueRef String] ValueRef #{3.0 3.1}]
   ;; Function
   [:GetNextFunction [ValueRef] ValueRef #{3.0 3.1}]
   [:GetPreviousFunction [ValueRef] ValueRef #{3.0 3.1}]
   [:DeleteFunction [ValueRef] Void #{3.0 3.1}]
   [:GetTypeKind [TypeRef] type-kind #{3.0 3.1}]
   [:GetTypeContext [TypeRef] ContextRef #{3.0 3.1}]
   [:Int1TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:Int8TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:Int16TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:Int32TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:Int64TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:IntTypeInContext [ContextRef Integer] TypeRef #{3.0 3.1}]
   [:Int1Type [] TypeRef #{3.0 3.1}]
   [:Int8Type [] TypeRef #{3.0 3.1}]
   [:Int16Type [] TypeRef #{3.0 3.1}]
   [:Int32Type [] TypeRef #{3.0 3.1}]
   [:Int64Type [] TypeRef #{3.0 3.1}]
   [:IntType [Integer] TypeRef #{3.0 3.1}]
   [:GetIntTypeWidth [TypeRef] Integer #{3.0 3.1}]
   [:FloatTypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:DoubleTypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:X86FP80TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:FP128TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:PPCFP128TypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:FloatType [] TypeRef #{3.0 3.1}]
   [:DoubleType [] TypeRef #{3.0 3.1}]
   [:X86FP80Type [] TypeRef #{3.0 3.1}]
   [:FP128Type [] TypeRef #{3.0 3.1}]
   [:PPCFP128Type [] TypeRef #{3.0 3.1}]
   [:FunctionType [TypeRef TypeRefPtr Integer bool] TypeRef #{3.0 3.1}]
   [:IsFunctionVarArg [TypeRef] bool #{3.0 3.1}]
   [:GetReturnType [TypeRef] TypeRef #{3.0 3.1}]
   [:CountParamTypes [TypeRef] Integer #{3.0 3.1}]
   [:GetParamTypes [TypeRef TypeRefPtr] Void #{3.0 3.1}]
   [:StructTypeInContext [ContextRef TypeRefPtr Integer bool] TypeRef #{3.0 3.1}]
   [:StructType [TypeRefPtr Integer bool] TypeRef #{3.0 3.1}]
   [:CountStructElementTypes [TypeRef] Integer #{3.0 3.1}]
   [:GetStructElementTypes [TypeRef TypeRefPtr] Void #{3.0 3.1}]
   [:IsPackedStruct [TypeRef] bool #{3.0 3.1}]
   [:ArrayType [TypeRef Integer] TypeRef #{3.0 3.1}]
   [:PointerType [TypeRef Integer] TypeRef #{3.0 3.1}]
   [:VectorType [TypeRef Integer] TypeRef #{3.0 3.1}]
   [:GetElementType [TypeRef] TypeRef #{3.0 3.1}]
   [:GetArrayLength [TypeRef] Integer #{3.0 3.1}]
   [:GetPointerAddressSpace [TypeRef] Integer #{3.0 3.1}]
   [:GetVectorSize [TypeRef] Integer #{3.0 3.1}]
   [:VoidTypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:LabelTypeInContext [ContextRef] TypeRef #{3.0 3.1}]
   [:VoidType [] TypeRef #{3.0 3.1}]
   [:LabelType [] TypeRef #{3.0 3.1}]
   [:TypeOf [ValueRef] TypeRef #{3.0 3.1}]
   [:GetValueName [ValueRef] String #{3.0 3.1}]
   [:SetValueName [ValueRef String] Void #{3.0 3.1}]
   [:DumpValue [ValueRef] Void #{3.0 3.1}]
   [:ReplaceAllUsesWith [ValueRef ValueRef] Void #{3.0 3.1}]
   [:HasMetadata [ValueRef] Integer #{3.0 3.1}]
   [:GetMetadata [ValueRef Integer] ValueRef #{3.0 3.1}]
   [:SetMetadata [ValueRef Integer ValueRef] Void #{3.0 3.1}]
   [:GetFirstUse [ValueRef] UseRef #{3.0 3.1}]
   [:GetNextUse [UseRef] UseRef #{3.0 3.1}]
   [:GetUser [UseRef] ValueRef #{3.0 3.1}]
   [:GetUsedValue [UseRef] ValueRef #{3.0 3.1}]
   [:GetOperand [ValueRef Integer] ValueRef #{3.0 3.1}]
   [:SetOperand [ValueRef Integer ValueRef] Void #{3.0 3.1}]
   [:ConstNull [TypeRef] ValueRef #{3.0 3.1}]
   [:ConstAllOnes [TypeRef] ValueRef #{3.0 3.1}]
   [:GetUndef [TypeRef] ValueRef #{3.0 3.1}]
   [:IsConstant [ValueRef] bool #{3.0 3.1}]
   [:IsNull [ValueRef] bool #{3.0 3.1}]
   [:IsUndef [ValueRef] bool #{3.0 3.1}]
   [:ConstPointerNull [TypeRef] ValueRef #{3.0 3.1}]
   [:MDStringInContext [ContextRef String Integer] ValueRef #{3.0 3.1}]
   [:MDString [String Integer] ValueRef #{3.0 3.1}]
   [:MDNodeInContext [ContextRef ValueRefPtr Integer] ValueRef #{3.0 3.1}]
   [:MDNode [ValueRefPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstInt [TypeRef Long bool] ValueRef #{3.0 3.1}]
   [:ConstIntOfString [TypeRef String BytePtr] ValueRef #{3.0 3.1}]
   [:ConstIntOfStringAndSize [TypeRef String Integer BytePtr] ValueRef #{3.0 3.1}]
   [:ConstReal [TypeRef Double] ValueRef #{3.0 3.1}]
   [:ConstRealOfString [TypeRef String] ValueRef #{3.0 3.1}]
   [:ConstRealOfStringAndSize [TypeRef String Integer] ValueRef #{3.0 3.1}]
   [:ConstIntGetZExtValue [ValueRef] Long #{3.0 3.1}]
   [:ConstIntGetSExtValue [ValueRef] Long #{3.0 3.1}]
   [:ConstStringInContext [ContextRef String Integer bool] ValueRef #{3.0 3.1}]
   [:ConstStructInContext [ContextRef ValueRefPtr Integer bool] ValueRef #{3.0 3.1}]
   [:ConstString [String Integer bool] ValueRef #{3.0 3.1}]
   [:ConstArray [TypeRef ValueRefPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstStruct [ValueRefPtr Integer bool] ValueRef #{3.0 3.1}]
   [:ConstVector [ValueRefPtr Integer] ValueRef #{3.0 3.1}]
   [:AlignOf [TypeRef] ValueRef #{3.0 3.1}]
   [:SizeOf [TypeRef] ValueRef #{3.0 3.1}]
   [:ConstNeg [ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNSWNeg [ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNUWNeg [ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFNeg [ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNot [ValueRef] ValueRef #{3.0 3.1}]
   [:ConstAdd [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNSWAdd [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNUWAdd [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFAdd [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstSub [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNSWSub [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNUWSub [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFSub [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstMul [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNSWMul [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstNUWMul [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFMul [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstUDiv [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstSDiv [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstExactSDiv [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFDiv [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstURem [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstSRem [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstFRem [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstAnd [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstOr [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstXor [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstShl [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstLShr [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstAShr [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstGEP [ValueRef IntegerPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstInBoundsGEP [ValueRef IntegerPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstTrunc [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstSExt [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstZExt [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstFPTrunc [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstFPExt [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstUIToFP [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstSIToFP [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstFPToUI [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstFPToSI [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstPtrToInt [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstIntToPtr [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstBitCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstZExtOrBitCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstSExtOrBitCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstTruncOrBitCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstPointerCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstIntCast [ValueRef TypeRef bool] ValueRef #{3.0 3.1}]
   [:ConstFPCast [ValueRef TypeRef] ValueRef #{3.0 3.1}]
   [:ConstSelect [ValueRef ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstExtractElement [ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstInsertElement [ValueRef ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstShuffleVector [ValueRef ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:ConstExtractValue [ValueRef IntegerPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstInsertValue [ValueRef ValueRef IntegerPtr Integer] ValueRef #{3.0 3.1}]
   [:ConstInlineAsm [TypeRef String String bool bool] ValueRef #{3.0 3.1}]
   [:BlockAddress [ValueRef BasicBlockRef] ValueRef #{3.0 3.1}]
   [:GetGlobalParent [ValueRef] ModuleRef #{3.0 3.1}]
   [:IsDeclaration [ValueRef] bool #{3.0 3.1}]
   [:GetLinkage [ValueRef] linkage #{3.0 3.1}]
   [:SetLinkage [ValueRef linkage] Void #{3.0 3.1}]
   [:GetSection [ValueRef] String #{3.0 3.1}]
   [:SetSection [ValueRef String] Void #{3.0 3.1}]
   [:GetVisibility [ValueRef] visibility #{3.0 3.1}]
   [:SetVisibility [ValueRef visibility] Void #{3.0 3.1}]
   [:GetAlignment [ValueRef] Integer #{3.0 3.1}]
   [:SetAlignment [ValueRef Integer] Void #{3.0 3.1}]
   [:GetNextGlobal [ValueRef] ValueRef #{3.0 3.1}]
   [:GetPreviousGlobal [ValueRef] ValueRef #{3.0 3.1}]
   [:DeleteGlobal [ValueRef] Void #{3.0 3.1}]
   [:GetInitializer [ValueRef] ValueRef #{3.0 3.1}]
   [:SetInitializer [ValueRef ValueRef] Void #{3.0 3.1}]
   [:IsThreadLocal [ValueRef] bool #{3.0 3.1}]
   [:SetThreadLocal [ValueRef bool] Void #{3.0 3.1}]
   [:IsGlobalConstant [ValueRef] bool #{3.0 3.1}]
   [:SetGlobalConstant [ValueRef bool] Void #{3.0 3.1}]
   [:GetIntrinsicID [ValueRef] Integer #{3.0 3.1}]
   [:GetFunctionCallConv [ValueRef] call-conv #{3.0 3.1}]
   [:SetFunctionCallConv [ValueRef call-conv] Void #{3.0 3.1}]
   [:GetGC [ValueRef] String #{3.0 3.1}]
   [:SetGC [ValueRef String] Void #{3.0 3.1}]
   [:AddFunctionAttr [ValueRef attribute] Void #{3.0 3.1}]
   [:GetFunctionAttr [ValueRef] attribute #{3.0 3.1}]
   [:RemoveFunctionAttr [ValueRef Long Long] Void #{3.0 3.1}]
   [:CountParams [ValueRef] Integer #{3.0 3.1}]
   [:GetParams [ValueRef ValueRefPtr] Void #{3.0 3.1}]
   [:GetParam [ValueRef Integer] ValueRef #{3.0 3.1}]
   [:GetParamParent [ValueRef] ValueRef #{3.0 3.1}]
   [:GetFirstParam [ValueRef] ValueRef #{3.0 3.1}]
   [:GetLastParam [ValueRef] ValueRef #{3.0 3.1}]
   [:GetNextParam [ValueRef] ValueRef #{3.0 3.1}]
   [:GetPreviousParam [ValueRef] ValueRef #{3.0 3.1}]
   [:AddAttribute [ValueRef Integer] Void #{3.0 3.1}]
   [:RemoveAttribute [ValueRef Integer] Void #{3.0 3.1}]
   [:GetAttribute [ValueRef] Integer #{3.0 3.1}]
   [:SetParamAlignment [ValueRef Integer] Void #{3.0 3.1}]
   [:BasicBlockAsValue [BasicBlockRef] ValueRef #{3.0 3.1}]
   [:ValueIsBasicBlock [ValueRef] bool #{3.0 3.1}]
   [:ValueAsBasicBlock [ValueRef] BasicBlockRef #{3.0 3.1}]
   [:GetBasicBlockParent [BasicBlockRef] ValueRef #{3.0 3.1}]
   [:CountBasicBlocks [ValueRef] Integer #{3.0 3.1}]
   [:GetBasicBlocks [ValueRef ValueRefPtr] Void #{3.0 3.1}]
   [:GetFirstBasicBlock [ValueRef] BasicBlockRef #{3.0 3.1}]
   [:GetLastBasicBlock [ValueRef] BasicBlockRef #{3.0 3.1}]
   [:GetNextBasicBlock [BasicBlockRef] BasicBlockRef #{3.0 3.1}]
   [:GetPreviousBasicBlock [BasicBlockRef] BasicBlockRef #{3.0 3.1}]
   [:GetEntryBasicBlock [ValueRef] BasicBlockRef #{3.0 3.1}]
   [:AppendBasicBlockInContext [ContextRef ValueRef String] BasicBlockRef #{3.0 3.1}]
   [:InsertBasicBlockInContext [ContextRef BasicBlockRef String] BasicBlockRef #{3.0 3.1}]
   [:AppendBasicBlock [ValueRef String] BasicBlockRef #{3.0 3.1}]
   [:InsertBasicBlock [BasicBlockRef String] BasicBlockRef #{3.0 3.1}]
   [:DeleteBasicBlock [BasicBlockRef] Void #{3.0 3.1}]
   [:GetInstructionParent [ValueRef] BasicBlockRef #{3.0 3.1}]
   [:GetFirstInstruction [BasicBlockRef] ValueRef #{3.0 3.1}]
   [:GetLastInstruction [BasicBlockRef] ValueRef #{3.0 3.1}]
   [:GetNextInstruction [ValueRef] ValueRef #{3.0 3.1}]
   [:GetPreviousInstruction [ValueRef] ValueRef #{3.0 3.1}]
   [:SetInstructionCallConv [ValueRef Integer] Void #{3.0 3.1}]
   [:GetInstructionCallConv [ValueRef] Integer #{3.0 3.1}]
   [:AddInstrAttribute [ValueRef Integer Integer] Void #{3.0 3.1}]
   [:RemoveInstrAttribute [ValueRef Integer Integer] Void #{3.0 3.1}]
   [:SetInstrParamAlignment [ValueRef Integer Integer] Void #{3.0 3.1}]
   [:IsTailCall [ValueRef] bool #{3.0 3.1}]
   [:SetTailCall [ValueRef bool] Void #{3.0 3.1}]
   [:AddIncoming [ValueRef ValueRefPtr BasicBlockRefPtr Integer] Void #{3.0 3.1}]
   [:CountIncoming [ValueRef] Integer #{3.0 3.1}]
   [:GetIncomingValue [ValueRef Integer] ValueRef #{3.0 3.1}]
   [:GetIncomingBlock [ValueRef Integer] BasicBlockRef #{3.0 3.1}]
   [:CreateBuilderInContext [ContextRef] BuilderRef #{3.0 3.1}]
   [:CreateBuilder [] BuilderRef #{3.0 3.1}]
   [:PositionBuilder [BuilderRef BasicBlockRef ValueRef] Void #{3.0 3.1}]
   [:PositionBuilderBefore [BuilderRef ValueRef] Void #{3.0 3.1}]
   [:PositionBuilderAtEnd [BuilderRef BasicBlockRef] Void #{3.0 3.1}]
   [:GetInsertBlock [BuilderRef] BasicBlockRef #{3.0 3.1}]
   [:ClearInsertionPosition [BuilderRef] Void #{3.0 3.1}]
   [:InsertIntoBuilder [BuilderRef ValueRef] Void #{3.0 3.1}]
   [:InsertIntoBuilderWithName [BuilderRef ValueRef String] Void #{3.0 3.1}]
   [:DisposeBuilder [BuilderRef] Void #{3.0 3.1}]
   [:SetCurrentDebugLocation [BuilderRef ValueRef] Void #{3.0 3.1}]
   [:GetCurrentDebugLocation [BuilderRef] ValueRef #{3.0 3.1}]
   [:SetInstDebugLocation [BuilderRef ValueRef] Void #{3.0 3.1}]
   [:BuildRetVoid [BuilderRef] ValueRef #{3.0 3.1}]
   [:BuildRet [BuilderRef ValueRef] ValueRef #{3.0 3.1}]
   [:BuildAggregateRet [BuilderRef ValueRefPtr Integer] ValueRef #{3.0 3.1}]
   [:BuildBr [BuilderRef BasicBlockRef] ValueRef #{3.0 3.1}]
   [:BuildCondBr [BuilderRef ValueRef BasicBlockRef BasicBlockRef] ValueRef #{3.0 3.1}]
   [:BuildSwitch [BuilderRef ValueRef BasicBlockRef Integer] ValueRef #{3.0 3.1}]
   [:BuildIndirectBr [BuilderRef ValueRef Integer] ValueRef #{3.0 3.1}]
   [:BuildInvoke [BuilderRef ValueRef ValueRefPtr Integer BasicBlockRef BasicBlockRef String] ValueRef #{3.0 3.1}]
   [:BuildLandingPad [BuilderRef TypeRef ValueRef Integer String] ValueRef #{3.0 3.1}]
   [:BuildResume [BuilderRef ValueRef] ValueRef #{3.0 3.1}]
   [:BuildUnreachable [BuilderRef] ValueRef #{3.0 3.1}]
   [:AddCase [ValueRef ValueRef BasicBlockRef] Void #{3.0 3.1}]
   [:AddDestination [ValueRef BasicBlockRef] Void #{3.0 3.1}]
   [:AddClause [ValueRef ValueRef] Void #{3.0 3.1}]
   [:SetCleanup [ValueRef bool] Void #{3.0 3.1}]
   [:BuildAdd [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNSWAdd [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNUWAdd [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFAdd [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildSub [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNSWSub [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNUWSub [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFSub [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildMul [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNSWMul [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNUWMul [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFMul [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildUDiv [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildSDiv [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildExactSDiv [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFDiv [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildURem [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildSRem [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFRem [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildShl [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildLShr [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildAShr [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildAnd [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildOr [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildXor [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildBinOp [BuilderRef opcode ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNeg [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNSWNeg [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNUWNeg [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFNeg [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildNot [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildMalloc [BuilderRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildArrayMalloc [BuilderRef TypeRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildAlloca [BuilderRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildArrayAlloca [BuilderRef TypeRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFree [BuilderRef ValueRef] ValueRef #{3.0 3.1}]
   [:BuildLoad [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildStore [BuilderRef ValueRef ValueRef] ValueRef #{3.0 3.1}]
   [:BuildGEP [BuilderRef ValueRef ValueRefPtr Integer String] ValueRef #{3.0 3.1}]
   [:BuildInBoundsGEP [BuilderRef ValueRef ValueRefPtr Integer String] ValueRef #{3.0 3.1}]
   [:BuildStructGEP [BuilderRef ValueRef Integer String] ValueRef #{3.0 3.1}]
   [:BuildGlobalString [BuilderRef String String] ValueRef #{3.0 3.1}]
   [:BuildGlobalStringPtr [BuilderRef String String] ValueRef #{3.0 3.1}]
   [:BuildTrunc [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildZExt [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildSExt [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildFPToUI [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildFPToSI [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildUIToFP [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildSIToFP [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildFPTrunc [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildFPExt [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildPtrToInt [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildIntToPtr [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildBitCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildZExtOrBitCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildSExtOrBitCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildTruncOrBitCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildCast [BuilderRef opcode ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildPointerCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildIntCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildFPCast [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildICmp [BuilderRef Integer ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildFCmp [BuilderRef Integer ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildPhi [BuilderRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildCall [BuilderRef ValueRef ValueRefPtr Integer String] ValueRef #{3.0 3.1}]
   [:BuildSelect [BuilderRef ValueRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildVAArg [BuilderRef ValueRef TypeRef String] ValueRef #{3.0 3.1}]
   [:BuildExtractElement [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildInsertElement [BuilderRef ValueRef ValueRef ValueRef] String ValueRef #{3.0 3.1}]
   [:BuildShuffleVector [BuilderRef ValueRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildExtractValue [BuilderRef ValueRef Integer String] ValueRef #{3.0 3.1}]
   [:BuildInsertValue [BuilderRef ValueRef ValueRef Integer String] ValueRef #{3.0 3.1}]
   [:BuildIsNull [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildIsNotNull [BuilderRef ValueRef String] ValueRef #{3.0 3.1}]
   [:BuildPtrDiff [BuilderRef ValueRef ValueRef String] ValueRef #{3.0 3.1}]
   [:IsATerminatorInst [ValueRef] ValueRef #{3.0 3.1}]
   [:WriteBitcodeToFile [ModuleRef String] Integer #{3.0 3.1}]
   [:CreateTargetData [String] TargetDataRef #{3.0 3.1}]
   [:AddTargetData [TargetDataRef PassManagerRef] Void #{3.0 3.1}]
   [:StoreSizeOfType [TargetDataRef TypeRef] Long #{3.0 3.1}]
   [:SizeOfTypeInBits [TargetDataRef TypeRef] Long #{3.0 3.1}]
   [:ABISizeOfType [TargetDataRef TypeRef] Integer #{3.0 3.1}]
   [:PreferredAlignmentOfType [TargetDataRef TypeRef] Integer #{3.0 3.1}]
   [:ABIAlignmentOfType [TargetDataRef TypeRef] Integer #{3.0 3.1}]
   [:CallFrameAlignmentOfType [TargetDataRef TypeRef] Integer #{3.0 3.1}]
   [:DisposeTargetData [TargetDataRef] Void #{3.0 3.1}]
   [:CreatePassManager [] PassManagerRef #{3.0 3.1}]
   [:DisposePassManager [PassManagerRef] Void #{3.0 3.1}]
   [:RunPassManager [PassManagerRef ModuleRef] bool #{3.0 3.1}]
   [:AddVerifierPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddGlobalOptimizerPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddIPSCCPPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddDeadArgEliminationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddInstructionCombiningPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddCFGSimplificationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddFunctionInliningPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddFunctionAttrsPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddScalarReplAggregatesPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddScalarReplAggregatesPassSSA [PassManagerRef] Void #{3.0 3.1}]
   [:AddJumpThreadingPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddConstantPropagationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddReassociatePass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLoopRotatePass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLICMPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLoopUnswitchPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLoopDeletionPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLoopUnrollPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddGVNPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddMemCpyOptPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddSCCPPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddDeadStoreEliminationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddStripDeadPrototypesPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddConstantMergePass [PassManagerRef] Void #{3.0 3.1}]
   [:AddArgumentPromotionPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddTailCallEliminationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddIndVarSimplifyPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddAggressiveDCEPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddGlobalDCEPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddCorrelatedValuePropagationPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddPruneEHPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddSimplifyLibCallsPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddLoopIdiomPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddEarlyCSEPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddTypeBasedAliasAnalysisPass [PassManagerRef] Void #{3.0 3.1}]
   [:AddBasicAliasAnalysisPass [PassManagerRef] Void #{3.0 3.1}]
   [:PassManagerBuilderCreate [] PassManagerBuilderRef #{3.0 3.1}]
   [:PassManagerBuilderDispose [PassManagerBuilderRef] Void #{3.0 3.1}]
   [:PassManagerBuilderSetOptLevel [PassManagerBuilderRef Integer] Void #{3.0 3.1}]
   [:PassManagerBuilderSetSizeLevel [PassManagerBuilderRef bool] Void #{3.0 3.1}]
   [:PassManagerBuilderSetDisableUnitAtATime [PassManagerBuilderRef bool] Void #{3.0 3.1}]
   [:PassManagerBuilderSetDisableUnrollLoops [PassManagerBuilderRef bool] Void #{3.0 3.1}]
   [:PassManagerBuilderSetDisableSimplifyLibCalls [PassManagerBuilderRef bool] Void #{3.0 3.1}]
   [:PassManagerBuilderUseInlinerWithThreshold [PassManagerBuilderRef Integer] Void #{3.0 3.1}]
   [:PassManagerBuilderPopulateModulePassManager [PassManagerBuilderRef PassManagerRef] Void #{3.0 3.1}]
   [:PassManagerBuilderPopulateFunctionPassManager [PassManagerBuilderRef PassManagerRef] Void #{3.0 3.1}]
   [:DisposeMemoryBuffer [MemoryBufferRef] Void #{3.0 3.1}]
   [:CreateObjectFile [MemoryBufferRef] ObjectFileRef #{3.0 3.1}]
   [:DisposeObjectFile [ObjectFileRef] Void #{3.0 3.1}]
   [:GetSections [ObjectFileRef] SectionIteratorRef #{3.0 3.1}]
   [:DisposeSectionIterator [SectionIteratorRef] Void #{3.0 3.1}]
   [:IsSectionIteratorAtEnd [ObjectFileRef SectionIteratorRef] bool #{3.0 3.1}]
   [:MoveToNextSection [SectionIteratorRef] Void #{3.0 3.1}]
   [:GetSectionName [SectionIteratorRef] String #{3.0 3.1}]
   [:GetSectionSize [SectionIteratorRef] Long #{3.0 3.1}]
   [:GetSectionContents [SectionIteratorRef] String #{3.0 3.1}]
   [:StructCreateNamed [ContextRef String] TypeRef #{3.0 3.1}]
   [:StructSetBody [TypeRef TypeRefPtr Integer bool] Void #{3.0 3.1}]
   [:ConstNamedStruct [TypeRef ValueRefPtr Integer] ValueRef #{3.0 3.1}]])
