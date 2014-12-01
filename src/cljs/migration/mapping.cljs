(ns migration.mapping
  (:require [migration.util :as util]))


(def v2->v3 {"Epoch" {:attributes  {;; v3 <- v2
                                    :start               :start
                                    :end                 :end
                                    :start_zone          :startZone
                                    :end_zone            :endZone
                                    :protocol_parameters (util/parameters :protocolParameters)
                                    :device_parameters   (util/parameters :deviceParameters)
                                    }
                      :links       {;; Per link, list of added. EXCLUDES _collaboration_roots
                                    :owner      [{:source_id :_id
                                                  :target_id :ownerUuid
                                                  :rel       "owner"}]
                                    :experiment [{:source_id :_id
                                                  :target_id :experiment
                                                  :rel       "experiment"}]
                                    :parent     [{:source_id :_id
                                                  :target_id :parent
                                                  :rel       "parent"}]}

                      :named_links {;; Per link, list of added. EXCLUDES _collaboration_roots
                                    :input_sources  (util/named-sources :inputSources "input_sources")

                                    :output_sources (util/named-sources :outputSources "output_sources")}}
             })


;(def example {:links {:rel {:added {:name        "optional rel name"
;                                    :source_id   "source entity uuid string"
;                                    :target_id   "target entity uuid id"
;                                    :rel         "relation"
;                                    :type        "Relation"
;                                    :inverse_rel "optional inverse rel"
;                                    }}}})
