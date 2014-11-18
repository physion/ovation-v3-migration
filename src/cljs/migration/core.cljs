(ns migration.core
  (:require [clojure.walk]))

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))

(def collaboration-roots "_collaboration_roots")

(defn convert-annotation-base
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

  (let [base (convert-annotation-base doc-v2)]
    (assoc base
      "_id"        (str "keywords_" (:_id doc-v2))
      "annotation_type" "keywords"
      "annotation" {"tag" (:tag doc-v2)})))


(defmulti convert-annotation :type)
(defmethod convert-annotation "TagAnnotation"
  [doc-v2]
  (convert-keyword-document doc-v2))


(defmulti convert :entity)
(defmethod convert false [annotation-doc]
  (convert-annotation annotation-doc))
