(ns low.api.value.global
  (:require [low.llvm :refer [LLVM]]))

(defn module [global]
  (LLVM :GetGlobalParent global))

(defn declaration? [global]
  (LLVM :IsDeclaration global))

(defn linkage [global]
  (LLVM :GetLinkage global))

(defn linkage! [global linkage]
  (LLVM :SetLinkage global linkage))

(defn section [global]
  (LLVM :GetSection global))

(defn section! [global section]
  (LLVM :SetSection global section))

(defn visibility [global]
  (LLVM :GetVisibility global))

(defn visibility! [global visibility]
  (LLVM :SetVisibility global visibility))

(defn alignment [global]
  (LLVM :GetAlignment global))

(defn alignment! [global bytes]
  (LLVM :SetAlignment global bytes))
