(ns low.api.pass-manager
  (:refer-clojure :exclude [type])
  (:require [clojure.string :as s]
            [low.llvm :refer [LLVM]]))

(defn create
  ([] (LLVM :CreatePassManager))
  ([module]
     (if (= (:type module) :module-ref)
          (LLVM :CreateFunctionPassManagerForModule module)
          (LLVM :CreateFunctionPassManager module))))

(defn destroy! [manager]
  (let [err (LLVM :FinalizeFunctionPassManager manager)]
    (LLVM :DisposePassManager manager)
    err))

(defmacro with-pass-managers [[& ctxs] & body]
  `(let [~@(mapcat #(list % `(create)) ctxs)]
     (try ~@body
          (finally ~@(map #(list `destroy! %) ctxs)))))

(defn initialize [manager]
  (LLVM :InitializeFunctionPassManager manager))

(defn run [manager module-or-function]
  (if (= (:type module-or-function) :module-ref)
    (LLVM :RunPassManager manager module-or-function)
    (LLVM :RunFunctionPassManager manager module-or-function)))

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
                                (str (s/join (map s/capitalize (s/split (name pass) #"-")))
                                     "Pass"))))]
     (apply LLVM pass manager args))))
