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

(defn named-link-docs
  [doc migration]
  (link-docs doc migration :named_links util/make-named-relation))

(defn convert-links
  [doc migration]
  (let [links (link-docs doc migration :links util/make-relation)
        named_links (named-link-docs doc migration)]

    (flatten [links named_links]))
  )

(defn convert-entity
  [doc]
  (if-let [migrations (mapping/v2->v3 (:type doc))]
    (let [docs (map (fn [migration]
                      (let [base {:_id         (if (nil? (:_id migration)) (:_id doc) ((:_id migration) doc)) ;; Allow migration to specify the _id
                                  :type        (if (nil? (:type migration)) (:type doc) (:type migration))
                                  :api_version mapping/api-version}

                            attributes {:attributes (into {} (map (fn [[v3 v2]]
                                                                    [v3 (v2 doc)]) (:attributes migration)))}
                            roots (if (empty? (:experimentIds doc))
                                    (:projectIds doc)
                                    (:experimentIds doc))

                            collab (if roots {:links {:_collaboration_roots roots}} {})
                            named_links (atom {})
                            named_link_docs (flatten (named-link-docs doc migration))

                            trash (if-let [info (:trash_info doc)] {:trash_info {:trashing_user (str "ovation://entities/" (:trashing_user info))
                                                                                 :trashing_date (:trashing_date info)
                                                                                 :trash_root    (str "ovation://entities/" (:trash_root info))}} {})

                            links (convert-links doc migration)]


                        ;(when (= (:type base) "Revision") (doall (map #(print "\n" % "\n") links)))
                        ;; update named_links from docs
                        (doall (map (fn [link]
                                      (let [rel (:rel link)
                                            name (:name link)
                                            entity-id (:source_id link)
                                            uri (util/make-named-link-uri entity-id rel name)
                                            ]
                                        (when (= entity-id (:_id doc))
                                          (swap! named_links assoc-in [:named_links (keyword rel) name :uri] uri)))) named_link_docs))

                        (flatten [(conj base attributes collab trash @named_links) links]))) migrations)]

      (flatten docs))))


(defn convert
  [doc user-id]

  (if (some #{(:type doc)} (keys mapping/v2->v3))
    (if (or (= user-id (:ownerUuid doc))
            (= user-id (:_id doc))) (convert-entity doc) [])
    (if (= user-id (:userId doc)) (conj '() (annotation/convert-annotation doc)) [])))
