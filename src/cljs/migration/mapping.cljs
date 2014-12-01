(ns migration.mapping
  (:require [migration.util :as util]))

(def api-version 3)

(def v2->v3 {"Epoch"  {:attributes  {;; v3 <- v2
                                     :start               :start
                                     :end                 :end
                                     :start_zone          :startZone
                                     :end_zone            :endZone
                                     :protocol_parameters (util/parameters :protocolParameters)
                                     :device_parameters   (util/parameters :deviceParameters)
                                     }
                       :links       {;; Per link, list of added.
                                     :owner      (fn [d] [{:source_id (:_id d)
                                                           :target_id (:ownerUuid d)
                                                           :rel       "owner"
                                                           :collaboration_roots (:experimentIds d)}])
                                     :experiment (fn [d] [{:source_id (:_id d)
                                                           :target_id (:experiment d)
                                                           :rel       "experiment"
                                                           :collaboration_roots (:experimentIds d)}])
                                     :parent     (fn [d] [{:source_id (:_id d)
                                                           :target_id (:experiment d)
                                                           :rel       "parent"
                                                           :collaboration_roots (:experimentIds d)}])
                                     :protocol   (fn [d] (if (:protocol d) [{:source_id (:_id d)
                                                                             :target_id (:protocol d)
                                                                             :rel       "protocol"
                                                                             :collaboration_roots (:experimentIds d)}] []))}

                       :named_links {;; Per link, list of added. EXCLUDES _collaboration_roots
                                     :input_sources  (util/named-sources :inputSources "input_sources")

                                     :output_sources (util/named-sources :outputSources "output_sources")}}

             "Source" {:attributes {;; v3 <- v2
                                    :label      :label
                                    :identifier :identifier}
                       :links      {;; Per link, list of added. EXCLUDES _collaboration_roots
                                    :owner    [{:source_id :_id
                                                :target_id :ownerUuid
                                                :rel       "owner"}]
                                    :children [{:source_id :_id
                                                :target_id :experiment
                                                :rel       "experiment"}]}
                       }
             })


;(def example {:links {:rel {:added {:name        "optional rel name"
;                                    :source_id   "source entity uuid string"
;                                    :target_id   "target entity uuid id"
;                                    :rel         "relation"
;                                    :type        "Relation"
;                                    :inverse_rel "optional inverse rel"
;                                    }}}})
