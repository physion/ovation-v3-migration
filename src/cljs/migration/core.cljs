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


(defmulti convert-annotation :type)
(defmethod convert-annotation "TagAnnotation"
  [doc-v2]
  (let [base (convert-annotation-base doc-v2)]
    (assoc base
      "_id" (str "keywords_" (:_id doc-v2))
      "annotation_type" "keywords"
      "annotation" {"tag" (:tag doc-v2)})))

(defmethod convert-annotation "PropertyAnnotation"
  [doc-v2]
  (let [base (convert-annotation-base doc-v2)]
    (assoc base
      "_id" (str "properties_" (:_id doc-v2))
      "annotation_type" "properties"
      "annotation" {"key"   (:key doc-v2)
                    "value" (:value doc-v2)})))
(defmethod convert-annotation "NoteAnnotation"
  [doc-v2]
  (let [base (convert-annotation-base doc-v2)]
    (assoc base
      "_id" (str "notes_" (:_id doc-v2))
      "annotation_type" "notes"
      "annotation" {"text"       (:text doc-v2)
                    "time_stamp" (:timestamp doc-v2)})))
(defmethod convert-annotation "TimelineAnnotation"
  [doc-v2]
  (let [base (convert-annotation-base doc-v2)]
    (assoc base
      "_id" (str "timeline_events_" (:_id doc-v2))
      "annotation_type" "timeline_events"
      "annotation" {"name"  (:name doc-v2)
                    "notes" (:notes doc-v2)
                    "start" (:start doc-v2)
                    "end"   (:end doc-v2)})))


(defmulti convert :entity)
(defmethod convert false
  [annotation-doc]
  (convert-annotation annotation-doc))
