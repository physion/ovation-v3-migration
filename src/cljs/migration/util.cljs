(ns migration.util)

(def collaboration-roots "_collaboration_roots")

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))
