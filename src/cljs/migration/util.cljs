(ns migration.util)

(def collaboration-roots "_collaboration_roots")

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))

(defn map-from-key-value-map-seq
  [params]
  (into {} (map (fn [m] [(:key m) (:value m)]) params)))
