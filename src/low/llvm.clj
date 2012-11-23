(ns low.llvm
  (:require [low.jna :refer [import-jna-function]])
  (:import (com.sun.jna Function Pointer)))

(def llvm-api [[:DoubleType Pointer 0]
               [:Int32Type Pointer 0]
               [:Int64Type Pointer 0]
               [:ConstString Pointer 3]
               [:RecompileAndRelinkFunction Pointer 2]
               [:ConstInt Pointer 3]
               [:ConstReal Pointer 2]
               [:ModuleCreateWithName Pointer 1]
               [:BuildCall Pointer 5]
               [:DisposeModule Boolean 1]
               [:BuildRet Pointer 2]
               [:DumpModule Void 1]
               [:WriteBitcodeToFile Integer 2]
               [:CreateBuilder Pointer 0]
               [:ModuleCreateWithName Pointer 1]
               [:BuildGlobalString Pointer 3]
               [:BuildGlobalStringPtr Pointer 3]
               [:AppendBasicBlock Pointer 2]
               [:PositionBuilderAtEnd Pointer 2]
               [:TypeOf Pointer 1]
               [:FunctionType Pointer 4]
               [:AddFunction Pointer 3]])

(def ^:private llvm-function-map (atom {}))

(defn setup-llvm [ver]
  (let [llvm-ver (str "LLVM-" ver)]
   (doseq [[f-name ret-type args-len] llvm-api]
     (swap! llvm-function-map assoc f-name
            (import-jna-function llvm-ver (str "LLVM" (name f-name)) ret-type args-len)))))

(defn LLVM [f & args]
  (apply (@llvm-function-map f) args))

