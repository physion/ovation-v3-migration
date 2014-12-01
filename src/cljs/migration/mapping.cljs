(ns migration.mapping)


(def v2->v3 {"Epoch" {:attributes  {:start              :start
                                    :end                :end
                                    :startZone          :start_zone
                                    :endZone            :end_zone
                                    :protocolParameters :protocol_parameters
                                    :deviceParameters   :device_parameters
                                    }
                      :links       {;; Per link, list of added. EXCLUDES _collaboration_roots
                                    :owner      [{:source_id :_id
                                                  :target_id :ownerUuid
                                                  :rel       "owner"
                                                  :type      "Relation"}]
                                    :experiment [{:source_id :_id
                                                  :target_id :experiment
                                                  :rel       "experiment"
                                                  :type      "Relation"}]
                                    :parent     [{:source_id :_id
                                                  :target_id :parent
                                                  :rel       "parent"
                                                  :type      "Relation"}]}

                      :named_links {:input_sources  ["a"]
                                    :output_sources ["a"]}}
             })


;(def example {:links {:rel {:added {:name        "optional rel name"
;                                    :source_id   "source entity uuid string"
;                                    :target_id   "target entity uuid id"
;                                    :rel         "relation"
;                                    :type        "Relation"
;                                    :inverse_rel "optional inverse rel"
;                                    }}}})
