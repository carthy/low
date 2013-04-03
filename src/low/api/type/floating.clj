(ns low.api.type.floating
  (:require [low.llvm :refer [llvm-version]]))

(def types
  "A map of valid floating types"
  (conj {:float "Float"
         :double "Double"
         :X86-FP80 "X86FP80"
         :FP128 "FP128"
         :PPC-FP128 "PPCFP128"}
        (when (>= @llvm-version 3.1)
          [:half "Half"])))
