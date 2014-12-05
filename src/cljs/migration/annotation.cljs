(ns migration.annotation
  (:require [migration.util :as util]))

(defn convert-annotation-base
  [doc]
  {
   "_rev"   (:_rev doc)
   "type"   "Annotation"
   "links"  {util/collaboration-roots (:experimentIds doc)}
   "user"   (util/make-entity-uri (:userId doc))
   "entity" (util/make-entity-uri (:entityId doc))
   "api_version" "3"
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


(defmethod convert-annotation nil
  [doc-v2]
  (print "Unknown document: " doc-v2))
