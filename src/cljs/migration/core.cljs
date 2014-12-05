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
              ;:_rev        (:_rev doc)
              :type        (:type doc)
              :api_version mapping/api-version}

        attributes {:attributes (into {} (map (fn [[v3 v2]]
                                                [v3 (v2 doc)]) (:attributes migration)))}
        roots (if (= (:type doc) "Project")
                (:projectIds doc)
                (:experimentIds doc))

        collab (if roots {:links {:_collaboration_roots roots}} {})

        trash (if-let [info (:trash_info doc)] {:trash_info {:trashing_user (str "ovation://entities/" (:trashing_user info))
                                                             :trashing_date (:trashing_date info)
                                                             :trash_root (str "ovation://entities/" (:trash_root info))}} {})]


    (print migration "\n")
    (print (conj base attributes collab trash) "\n")
    (flatten [(conj base attributes collab trash) (convert-links doc migration)])))


(defn convert
  [doc]

  (if (some #{(:type doc)} (keys mapping/v2->v3))
    (convert-entity doc)
    (conj '() (annotation/convert-annotation doc))))
