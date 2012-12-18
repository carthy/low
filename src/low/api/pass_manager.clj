(ns low.api.pass-manager
  (:refer-clojure :exclude [type])
  (:require [clojure.utils :refer [camel-case]]
            [low.llvm :refer [LLVM]]))

(defn create
  ([] (LLVM :CreatePassManager))
  ([module]
     (LLVM (if (= (:type module) :module)
             :CreateFunctionPassManagerForModule
             :CreateFunctionPassManager)
           module)))

(defn destroy! [manager]
  (let [err (LLVM :FinalizeFunctionPassManager manager)]
    (LLVM :DisposePassManager manager)
    err))

(defmacro with-destroy [[& ctxs] & body]
  `(let [~@ctxs]
     (try ~@body
          (finally ~@(map #(list `destroy! %) (take-nth 2 ctxs))))))

(defn initialize [manager]
  (LLVM :InitializeFunctionPassManager manager))

(defn run [manager module-or-function]
  (LLVM (if (= (:type module-or-function) :module)
          :RunPassManager
          :RunFunctionPassManager)
        manager module-or-function))

(def valid-passes
  #{:always-inline :argument-promotion :constant-merge :dead-arg-elimination
    :function-attrs :function-inlining :global-dce :global-optimizer
    :ip-constant-propagation :prune-eh :ipsccp :internalize
    :strip-dead-prototypes :strip-symbols :agressive-dce
    :early-cse :type-based-alias-analysis :basic-alias-analysis
    :cfg-simplification :dead-store-elimination :gvn :ind-var-simplify
    :instruction-combining :jump-threading :licm :loop-deletion
    :loop-rotate :loop-unroll :loop-unswitch :mem-cpy-opt
    :promote-memory-to-register :reassociate :sccp :lower-expect-intrinsics
    :scalar-repl-aggregates-with-threshold :scalar-repl-aggregates
    :scalar-repl-aggregates-ssa :correlated-value-propagation
    :simplify-lib-calls :tail-call-elimination :loop-idiom
    :constant-propagation :demote-memory-to-register :verifier
    :bb-vectorize :loop-vectorize})

(def ^:private special-pass-name
  {:globa-dce "GlobalDCEPass"
   :ip-constant-propagation "IPConstantPropagationPass"
   :bb-vectorize "BBVectorizePass"
   :prune-eh "PruneEHPass"
   :ipsccp "IPSCCPPass"
   :agressive-dce "AggressiveDCEPass"
   :early-cse "EarlyCSEPass"
   :cfg-simplification "CFGSimplificationPass"
   :gnv "GNVPass"
   :licm "LICMPass"
   :scalar-repl-aggregates-ssa "ScalarReplAggregatesPassSSA"
   :scalar-repl-aggregates-with-threshold "ScalarReplAggregatesPassWithThreshold"
   :sscp "SSCPPass"})

(defn add-pass [manager pass & args]
  (when (valid-passes pass)
   (let [pass (keyword (str "Add"
                            (or (special-pass-name pass)
                                (str (name (camel-case pass)) "Pass"))))]
     (apply LLVM pass manager args))))
