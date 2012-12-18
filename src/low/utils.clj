(ns low.utils
  (:require [clojure.string :as s]))

(defn get-keys [coll]
  (if (map? coll)
    (keys coll)
    (range (count coll))))

(defn get-values [coll]
  (if (map? coll)
    (vals coll)
    coll))

(defn came-case [kw]
  (keyword (s/join (map s/capitalize (s/split (name kw) #"-")))))
