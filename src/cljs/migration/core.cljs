(ns migration.core
  (:require [clojure.walk]
            [migration.annotation :as annotation]
            [migration.util :as util]
            [migration.mapping :as mapping]))



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
        base {:_id         (:_id doc)
              :_rev        (:_rev doc)
              :type        (:type doc)
              :api_version mapping/api-version}

        attributes {:attributes (into {} (map (fn [[v3 v2]]
                                                [v3 (v2 doc)]) (:attributes migration)))}
        collab {:links {:_collaboration_roots (if (= (:type doc) "Project")
                                                (:projectIds doc)
                                                (:experimentIds doc))}}

        trash (if-let [info (:trash_info doc)] {:trash_info {:trashing_user (str "ovation://entities/" (:trashing_user info))
                                                             :trashing_date (:trashing_date info)
                                                             :trash_root (str "ovation://entities/" (:trash_root info))}} {})]


    (flatten [(conj base attributes collab trash) (convert-links doc migration)])))


(defmulti convert :entity)
(defmethod convert false
  [annotation-doc]
  (conj '() (annotation/convert-annotation annotation-doc)))

(defmethod convert true
  [entity-doc]
  (convert-entity entity-doc))

(defn ^:export migrate
  [doc]
  (convert doc))
