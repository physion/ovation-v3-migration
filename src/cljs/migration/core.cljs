(ns migration.core
  (:require [clojure.walk]
            [migration.annotation :as annotation]
            [migration.util :as util]
            [migration.mapping :as mapping]))


;(defn convert-base
;  [doc]
;  {
;   "_id"         (:_id doc)
;   "_rev"        (:_rev doc)
;   "type"        (:type doc)
;   "links"       {util/collaboration-roots (:experimentIds doc)}
;   "owner"       (util/make-entity-uri (:ownerUuid doc))
;   "api_version" "3"
;   })
;
;(defn deep-merge
;  [base doc]
;  (merge-with merge base doc))
;
;(defn add-timeline-element
;  [doc-v2 doc]
;  (let [start {"start"      (:start doc-v2)
;               "start_zone" (:startZone doc-v2)}
;        attributes (if (contains? doc-v2 :end)
;                     (conj start {"end"      (:end doc-v2)
;                                  "end_zone" (:endZone doc-v2)})
;                     start)]
;    (deep-merge doc {"attributes" attributes})))
;
;(defn add-procedure-element
;  [doc-v2 doc]
;  (deep-merge doc {"attributes" {"protocol_parameters" (util/map-from-key-value-map-seq (:protocolParameters doc-v2))
;                                 "device_parameters"   (util/map-from-key-value-map-seq (:deviceParameters doc-v2))}}))
;
;(defmulti add-links :type)
;(defmethod add-links "Epoch"
;  [doc-v2 doc]
;  (concat [doc] '()))
;
;(defmulti convert-entity :type)
;(defmethod convert-entity "Project"
;  [doc]
;  (->> doc
;       (convert-base)
;       (add-timeline-element doc)))
;
;(defmethod convert-entity "Epoch"
;  [doc]
;  (->> doc
;       (convert-base)
;       (add-timeline-element doc)
;       (add-procedure-element doc)
;       (add-links doc)))

(defn link-docs
  [doc migration key reln-fn]
  (map (fn [[rel target-fn]]
         (map (fn [link-description] (reln-fn link-description)) (target-fn doc)))
       (key migration)))

(defn convert-links
  [doc migration]
  (let [links (link-docs doc migration :links util/make-relation)
        named_links (link-docs doc migration :named_links util/make-named-relation)]

    (flatten [links named_links]))
  )

(defn convert-entity
  [doc]
  (let [migration (mapping/v2->v3 (:type doc))
        base {:_id (:_id doc)
              :_rev (:_rev doc)
              :api_version mapping/api-version}

        attributes {:attributes (into {} (map (fn [[v3 v2]]
                                                [v3 (v2 doc)]) (:attributes migration)))}
        collab {:links {:_collaboration_roots (:experimentIds doc)}}]


    (flatten [(conj base attributes collab) (convert-links doc migration)])))

(defmulti convert :entity)
(defmethod convert false
  [annotation-doc]
  (conj '() (annotation/convert-annotation annotation-doc)))

(defmethod convert true
  [entity-doc]
  (convert-entity entity-doc))
