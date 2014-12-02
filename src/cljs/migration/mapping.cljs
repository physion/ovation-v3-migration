(ns migration.mapping
  (:require [migration.util :as util]))

(def api-version 3)

(def owner-link (fn [d] [{:source_id           (:_id d)
                          :target_id           (:ownerUuid d)
                          :rel                 "owner"
                          :collaboration_roots (:experimentIds d)}])
  )
(def v2->v3 {"Epoch"          {:attributes  {;; v3 <- v2
                                             :start               :start
                                             :end                 :end
                                             :start_zone          :startZone
                                             :end_zone            :endZone
                                             :protocol_parameters (util/parameters :protocolParameters)
                                             :device_parameters   (util/parameters :deviceParameters)
                                             }
                               :links       {;; Per link, list of added.
                                             :owner      owner-link
                                             :experiment (fn [d] [{:source_id           (:_id d)
                                                                   :target_id           (:experiment d)
                                                                   :rel                 "experiment"
                                                                   :collaboration_roots (:experimentIds d)}])
                                             :parent     (fn [d] [{:source_id           (:_id d)
                                                                   :target_id           (:experiment d)
                                                                   :rel                 "parent"
                                                                   :collaboration_roots (:experimentIds d)}])
                                             :protocol   (fn [d] (if (:protocol d) [{:source_id           (:_id d)
                                                                                     :target_id           (:protocol d)
                                                                                     :rel                 "protocol"
                                                                                     :collaboration_roots (:experimentIds d)}] []))}

                               :named_links {;; Per link, list of added. EXCLUDES _collaboration_roots
                                             :input_sources  (util/named-targets :inputSources "input_sources")

                                             :output_sources (util/named-targets :outputSources "output_sources")}}

             "Source"         {:attributes  {;; v3 <- v2
                                             :label      :label
                                             :identifier :identifier
                                             :is_root    #(nil? (:parentEpoch %))}
                               :links       {;; Per link, list of added. EXCLUDES _collaboration_roots
                                             :owner               owner-link
                                             :children            (fn [d] (map (fn [child] {:source_id           (:_id d)
                                                                                            :target_id           child
                                                                                            :rel                 "children"
                                                                                            :inverse_rel         "parents"
                                                                                            :collaboration_roots (:experimentIds d)}) (:childrenSources d)))

                                             :producing_procedure (fn [d] (if-let [epoch (:parentEpoch d)] [{:source_id           (:_id d)
                                                                                                             :target_id           epoch
                                                                                                             :rel                 "producing_procedure"
                                                                                                             :collaboration_roots (:experimentIds d)}]
                                                                                                           []))}
                               :named_links {}
                               }

             "User"           {:attributes  (:attributes {;; v3 <- v2
                                                          :name             :username
                                                          :email            :email
                                                          :password_hash    :passwordHash
                                                          :hash_algorithm   :digestAlgorithm
                                                          :pkcs5_iterations :pcks5Iterations
                                                          :salt             :password_salt
                                                          :pepper           :pepper})
                               :links       {}
                               :named_links {}}

             "AnalysisRecord" {:attributes  {;; v3 <- v2
                                             :name                :name
                                             :protocol_parameters (util/parameters :protocolParameters)
                                             :device_parameters   (util/parameters :deviceParameters)
                                             }
                               :links       {:owner    owner-link
                                             :parent   (fn [d] [{:source_id           (:_id d)
                                                                 :target_id           (:experiment d)
                                                                 :rel                 "parent"
                                                                 :collaboration_roots (:experimentIds d)}])
                                             :protocol (fn [d] (if (:protocol d) [{:source_id           (:_id d)
                                                                                   :target_id           (:protocol d)
                                                                                   :rel                 "protocol"
                                                                                   :collaboration_roots (:experimentIds d)}] []))}

                               :named_links {:inputs  (util/named-targets :inputs "inputs")

                                             :outputs (util/named-targets :outputs "outputs")
                                             }}

             "Resource"       {:attributes  {;; v3 <- v2
                                             :name                 :name
                                             :content_type         :mimeType
                                             :data_url             :dataUri
                                             :file_name            :filename
                                             :supporting_file_urls :supportingFiles
                                             }

                               :links       {:owner             owner-link
                                             :containing_entity (fn [d] [{:source_id           (:_id d)
                                                                          :target_id           (:containingEntity d)
                                                                          :rel                 "parent"
                                                                          :collaboration_roots (:experimentIds d)}])}

                               :named_links {}}
             })
