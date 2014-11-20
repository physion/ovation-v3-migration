(ns migration.core
  (:require [clojure.walk]
            [migration.annotation :as annotation]
            [migration.util :as util]))


(defn convert-base
  [doc]
  {
   "_id"         (:_id doc)
   "_rev"        (:_rev doc)
   "type"        (:type doc)
   "links"       {util/collaboration-roots (:experimentIds doc)}
   "owner"       (util/make-entity-uri (:ownerUuid doc))
   "api_version" "3"
   })

(defn add-timeline-element
  [doc-v2 doc]
  (let [start {"start"      (:start doc-v2)
               "start_zone" (:startZone doc-v2)}
        attributes (if (contains? doc-v2 :end)
                     (conj start {"end"      (:end doc-v2)
                                  "end_zone" (:endZone doc-v2)})
                     start)]
    (merge-with merge doc {"attributes" attributes})))

(defmulti convert-entity :type)
(defmethod convert-entity "Project"
  [doc]
  (->> doc
       (convert-base)
       (add-timeline-element doc)))

(defmulti convert :entity)
(defmethod convert false
  [annotation-doc]
  (annotation/convert-annotation annotation-doc))

(defmethod convert true
  [entity-doc]
  (convert-entity entity-doc))
