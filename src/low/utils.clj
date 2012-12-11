(ns low.utils)

(defn get-keys [coll]
  (if (map? coll)
    (keys coll)
    (range (count coll))))

(defn get-values [coll]
  (if (map? coll)
    (vals coll)
    coll))
