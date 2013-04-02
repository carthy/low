(ns low.utils
  (:refer-clojure :exclude [keys vals])
  (:require [clojure.string :as s])
  (:alias c.c clojure.core))

(defn keys [coll]
  "Like clojure.core/keys but works on lists and vectors too"
  (if (map? coll)
    (c.c/keys coll)
    (range (count coll))))

(defn vals [coll]
  "Like clojure.core/vals but works on lists and vectors too"
  (if (map? coll)
    (c.c/vals coll)
    coll))

(defn camel-case [kw]
  {:pre [(keyword? kw)]}
  (->> (-> kw name (s/split #"-"))
       (map s/capitalize)
       s/join
       keyword))

(defn pos-or [f]
  (fn [x] (if (neg? x) (f x) x)))
