(ns migration.test.links-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.util :as util]))


(describe "Link documents"
          (it "should have _id"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= "123-456--rel-name-->789-012"
                         (:_id (util/make-relation {:rel                 rel
                                                    :inverse_rel         inverse
                                                    :source_id           source
                                                    :target_id           target
                                                    :collaboration_roots collab})))))

          (it "should have type"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= "Relation"
                         (:type (util/make-relation {:rel                 rel
                                                     :inverse_rel         inverse
                                                     :source_id           source
                                                     :target_id           target
                                                     :collaboration_roots collab})))))

          (it "should have rel"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= rel
                         (:rel (util/make-relation {:rel                  rel
                                                    :inverse_rel          inverse
                                                    :source_id            source
                                                    :target_id            target
                                                    :coollaboration_roots collab})))))

          (it "should have inverse rel"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= inverse
                         (:inverse_rel (util/make-relation {:rel                 rel
                                                            :inverse_rel         inverse
                                                            :source_id           source
                                                            :target_id           target
                                                            :collaboration_roots collab})))))

          (it "should have source_id"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= source
                         (:source_id (util/make-relation {:rel                 rel
                                                          :inverse_rel         inverse
                                                          :source_id           source
                                                          :target_id           target
                                                          :collaboration_roots collab})))))


          (it "should have target_id"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= target
                         (:target_id (util/make-relation {:rel                 rel
                                                          :inverse_rel         inverse
                                                          :source_id           source
                                                          :target_id           target
                                                          :collaboration_roots collab})))))


          (it "should have links._collaboration_roots"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= collab
                         (get-in (util/make-relation {:rel                 rel
                                                      :inverse_rel         inverse
                                                      :source_id           source
                                                      :target_id           target
                                                      :collaboration_roots collab})
                                 [:links :_collaboration_roots]))))
          )


(describe "Named Link documents"
          (it "should have _id"
              (let [rel "rel_name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= "123-456--rel_name>namey-->789-012"
                         (:_id (util/make-named-relation {:rel                 rel
                                                          :inverse_rel         inverse
                                                          :source_id           source
                                                          :target_id           target
                                                          :name                name
                                                          :collaboration_roots collab})))))

          (it "should have type"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= "Relation"
                         (:type (util/make-named-relation {:rel                 rel
                                                           :inverse_rel         inverse
                                                           :source_id           source
                                                           :target_id           target
                                                           :name                name
                                                           :collaboration_roots collab})))))

          (it "should have rel"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= rel
                         (:rel (util/make-named-relation {:rel                 rel
                                                          :inverse_rel         inverse
                                                          :source_id           source
                                                          :target_id           target
                                                          :name                name
                                                          :collaboration_roots collab})))))

          (it "should have inverse rel"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= inverse
                         (:inverse_rel (util/make-named-relation {:rel                 rel
                                                                  :inverse_rel         inverse
                                                                  :source_id           source
                                                                  :target_id           target
                                                                  :name                name
                                                                  :collaboration_roots collab})))))

          (it "should have source_id"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= source
                         (:source_id (util/make-named-relation {:rel                 rel
                                                                :inverse_rel         inverse
                                                                :source_id           source
                                                                :target_id           target
                                                                :name                name
                                                                :collaboration_roots collab})))))


          (it "should have target_id"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= target
                         (:target_id (util/make-named-relation {:rel                 rel
                                                                :inverse_rel         inverse
                                                                :source_id           source
                                                                :target_id           target
                                                                :name                name
                                                                :collaboration_roots collab})))))

          (it "should have name"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]
                    name "namey"]

                (should= name
                         (:name (util/make-named-relation {:rel                 rel
                                                           :inverse_rel         inverse
                                                           :source_id           source
                                                           :target_id           target
                                                           :name                name
                                                           :collaboration_roots collab})))))

          (it "should have links._collaboration_roots"
              (let [rel "rel-name"
                    inverse "inverse-rel-name"
                    source "123-456"
                    target "789-012"
                    collab ["123", "456"]]

                (should= collab
                         (get-in (util/make-named-relation {:rel                 rel
                                                            :inverse_rel         inverse
                                                            :source_id           source
                                                            :target_id           target
                                                            :name                name
                                                            :collaboration_roots collab})
                                 [:links :_collaboration_roots]))))
          )

