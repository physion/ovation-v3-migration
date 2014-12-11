(ns migration.util
  (:require [cljs-uuid.core :as uuid]))

(def collaboration-roots "_collaboration_roots")

(defn collab-roots
  "Experiment Ids or project Ids if no experiments"
  [doc]
  (if-let [expts (:experimentIds doc)]
    expts
    (:projectIds doc)))

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))

(defn map-from-key-value-map-seq
  [params]
  (into {} (map (fn [m] [(:key m) (:value m)]) params)))

(def rel-base {:type "Relation"})

(defn random-uuid
  "Generates a pseudo-random UUID, returning its string form"
  []
  (str (uuid/make-random)))

(defn make-rel-id
  [source rel target]
  (str source "--" rel "-->" target))
;{
; "_id": "617a54ed-77c1-4964-9e52-49779b4dcc8f--epoch-->196dfbdb-eb00-42f5-bc42-699dc2404bc4",
;"_rev": "1-687aa3f96159bc30889fbdb8e81ca3b2",
;"inverse_rel": "measurements",
;"rel": "epoch",
;"target_id": "196dfbdb-eb00-42f5-bc42-699dc2404bc4",
;"links" {
;          "_collaboration_roots": [
;                                   "678544f2-0a3b-40a1-8b7e-3c3febb9c7f4"

;                                   ]
;                                  ,
;"source_id": "617a54ed-77c1-4964-9e52-49779b4dcc8f",
;"type": "Relation"
;}
(defn make-relation
  "Makes a relation given arguments map"
  [opts]
  (if (= (:type opts) "Annotation")
    opts                                                    ;; annotation

    (let [{:keys [rel inverse_rel target_id source_id collaboration_roots]} opts
          common (assoc rel-base :_id (make-rel-id source_id rel target_id)
                                 :rel rel
                                 :target_id target_id
                                 :source_id source_id
                                 :links {:_collaboration_roots collaboration_roots})]

      ;; Add inverse_rel only if present
      (if inverse_rel
        (assoc common :inverse_rel inverse_rel)
        common))))


(defn make-named-rel-id
  "49754fa6-df69-452e-8808-24e7d12c5bf6--inputs>Input 1-->35c67823-0f38-4566-9585-ae4a104ca662"
  [source rel name target]
  (str source "--" rel ">" name "-->" target))

(defn make-named-relation
  "Makes a named relation given arguments map"
  [opts]
  (let [{:keys [rel name target_id source_id]} opts]
    (assoc (make-relation opts)
      :_id (make-named-rel-id source_id rel name target_id)
      :name name)))

(defn parameters
  "mapping helper for protocol/device parameters"
  [key]
  (fn [doc]
    (map-from-key-value-map-seq (key doc))))

(defn named-targets
  "mapping helper for input/output sources (and other key/value named relationships)"
  [key rel & [inverse_rel]]
  (fn [doc]
    (map (fn [src] (let [rel-doc {:source_id           (:_id doc)
                                  :target_id           (:value src)
                                  :name                (:key src)
                                  :rel                 rel
                                  :collaboration_roots (:experimentIds doc)}]
                     (if inverse_rel
                       (assoc rel-doc :inverse_rel inverse_rel)
                       rel-doc)))
         (key doc))))
