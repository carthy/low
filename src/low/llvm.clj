(ns low.llvm
  (:require [low.jna :refer [import-jna-function]])
  (:import (com.sun.jna Function Pointer)))

(def llvm-api '[[LLVMDoubleType Pointer 0]
                [LLVMInt32Type Pointer 0]
                [LLVMInt64Type Pointer 0]
                [LLVMConstString Pointer 3]
                [LLVMRecompileAndRelinkFunction Pointer 2]
                [LLVMConstInt Pointer 3]
                [LLVMConstReal Pointer 2]
                [LLVMModuleCreateWithName Pointer 1]
                [LLVMBuildCall Pointer 5]
                [LLVMDisposeModule Boolean 1]
                [LLVMBuildRet Pointer 2]
                [LLVMDumpModule Void 1]
                [LLVMWriteBitcodeToFile Integer 2]
                [LLVMCreateBuilder Pointer 0]
                [LLVMModuleCreateWithName Pointer 1]
                [LLVMBuildGlobalString Pointer 3]
                [LLVMBuildGlobalStringPtr Pointer 3]
                [LLVMGetTypeName Pointer 2]
                [LLVMAppendBasicBlock Pointer 2]
                [LLVMPositionBuilderAtEnd Pointer 2]
                [LLVMTypeOf Pointer 1]
                [LLVMOpaqueType Pointer 0]
                [LLVMFunctionType Pointer 4]
                [LLVMAddFunction Pointer 3]])

(defmacro import-llvm-functions []
  `(do
     ~@(for [[f-name ret-type arg-len] llvm-api]
         (list 'import-jna-function 'LLVM-3.2 f-name (eval ret-type) arg-len))))

(import-llvm-functions)
