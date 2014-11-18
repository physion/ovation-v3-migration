(ns migration.core
  (:require [clojure.walk]))

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))


;; TODO defmulti for :type

(def collaboration-roots "_collaboration_roots")

(defn convert-annotation
  [doc]
  {
   "_rev"   (:_rev doc)
   "type"   "Annotation"
   "links"  {collaboration-roots (:experimentIds doc)}
   "user"   (make-entity-uri (:userId doc))
   "entity" (make-entity-uri (:entityId doc))
   })


(defn convert-keyword-document
  "Converts an annotation document"
  [doc-v2]

  (let [doc (clojure.walk/keywordize-keys doc-v2)
        base (convert-annotation doc)]
    (assoc base
      "_id"        (str "keywords_" (:_id doc))
      "annotation" {"tag" (:tag doc)})))
