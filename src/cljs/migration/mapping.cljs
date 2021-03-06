(ns migration.mapping
  (:require [migration.util :as util]))

(def api-version util/api-version)

(def owner-link (fn [d] [{:source_id           (:_id d)
                          :target_id           (:ownerUuid d)
                          :rel                 "owner"
                          :collaboration_roots (util/collab-roots d)}]))

(def protocol-link (fn [d] (if (:protocol d) [{:source_id           (:_id d)
                                               :target_id           (:protocol d)
                                               :rel                 "protocol"
                                               :inverse_rel         "procedures"
                                               :collaboration_roots (util/collab-roots d)}] [])))



(def v2->v3 {"Epoch"          [{:attributes  {;; v3 <- v2
                                              :start               :start
                                              :end                 :end
                                              :start_zone          :startZone
                                              :end_zone            :endZone
                                              :protocol_parameters (util/parameters :protocolParameters)
                                              :device_parameters   (util/parameters :deviceParameters)
                                              }
                                :links       {;; Per link, list of added.
                                              :owner      owner-link
                                              :experiment (fn [d] (let [base {:source_id           (:_id d) ;; TODO don't have inverse if in an EpochGroup
                                                                              :target_id           (:experiment d)
                                                                              :rel                 "experiment"
                                                                              :collaboration_roots (util/collab-roots d)}]
                                                                    (if (= (:experiment d) (:parent d))
                                                                      [(assoc base :inverse_rel "epochs")]
                                                                      [base])
                                                                    ))
                                              :parent     (fn [d] [{:source_id           (:_id d)
                                                                    :target_id           (:parent d)
                                                                    :rel                 "parent"
                                                                    :inverse_rel         "epochs"
                                                                    :collaboration_roots (util/collab-roots d)}])
                                              :protocol   protocol-link}

                                :named_links {;; Per link, list of added. EXCLUDES _collaboration_roots
                                              :input_sources  (util/named-targets :inputSources "input_sources" "epochs")

                                              :output_sources (util/named-targets :outputSources "output_sources" "producing_procedure")}}]

             "Source"         [{:attributes  {;; v3 <- v2
                                              :label      :label
                                              :identifier :identifier
                                              :is_root    :is_root ;; Provided by migration.js harness after call to _design/EntityBase/parent_sources. Was #(nil? (:parentEpoch %))
                                              }
                                :links       {;; Per link, list of added. EXCLUDES _collaboration_roots
                                              :owner               owner-link
                                              :children            (fn [d] (map (fn [child] {:source_id           (:_id d)
                                                                                             :target_id           child
                                                                                             :rel                 "children"
                                                                                             :inverse_rel         "parents"
                                                                                             :collaboration_roots (util/collab-roots d)}) (:childrenSources d)))

                                              :producing_procedure (fn [d] (if-let [epoch (:parentEpoch d)] [{:source_id           (:_id d)
                                                                                                              :target_id           epoch
                                                                                                              :rel                 "producing_procedure"
                                                                                                              :collaboration_roots (util/collab-roots d)}]
                                                                                                            []))}
                                :named_links {}
                                }]

             "User"           [{:attributes  {;; v3 <- v2
                                              :name             :username
                                              :email            :email
                                              :password_hash    :passwordHash
                                              :hash_algorithm   :digestAlgorithm
                                              :pkcs5_iterations :pkcs5Iterations
                                              :salt             :password_salt
                                              :pepper           :pepper}
                                :links       {}
                                :named_links {}}]

             "AnalysisRecord" [{:attributes  {;; v3 <- v2
                                              :name                :name
                                              :protocol_parameters (util/parameters :protocolParameters)
                                              :device_parameters   (util/parameters :deviceParameters)
                                              }
                                :links       {:owner    owner-link
                                              :parent   (fn [d] [
                                                                 ;; Relation document
                                                                 {:source_id           (:_id d)
                                                                  :target_id           (:parent d)
                                                                  :rel                 "parent"
                                                                  :collaboration_roots (util/collab-roots d)}

                                                                 ;; Annotation document
                                                                 {:_id             (str "analysis_records_" (util/random-uuid))
                                                                  :type            "Annotation"
                                                                  :links           {:_collaboration_roots (util/collab-roots d)}
                                                                  :user            (util/make-entity-uri (:ownerUuid d))
                                                                  :entity          (util/make-entity-uri (:parent d))
                                                                  :api_version     api-version
                                                                  :annotation_type "analysis_records"
                                                                  :annotation      {:uri (util/make-entity-uri (:_id d))}
                                                                  }])
                                              :protocol protocol-link}

                                :named_links {:inputs  (util/named-targets :inputs "inputs" "analyses")

                                              :outputs (util/named-targets :outputs "outputs" "containing_entity")
                                              }}]

             "Resource"       [{:attributes  {;; v3 <- v2
                                              :label :name
                                              }

                                :links       {:owner owner-link}

                                :named_links {}}

                               {
                                :type        "Revision"
                                :_id         (fn [d] (str (:_id d) "-rev1"))

                                :attributes  {;; v3 <- v2
                                              :content_type         :mimeType
                                              :data_url             :dataUri
                                              :file_name            :filename
                                              :supporting_file_urls :supportingFiles
                                              :head                 (fn [_] true)
                                              :version              (fn [_] 1)
                                              :resource             :_id ;; cached _id of Resource
                                              }

                                :links       {:owner    (fn [d] [{:source_id           (str (:_id d) "-rev1")
                                                                  :target_id           (:ownerUuid d)
                                                                  :rel                 "owner"
                                                                  :collaboration_roots (util/collab-roots d)}])
                                              :resource (fn [d]
                                                          [{:source_id           (str (:_id d) "-rev1")
                                                            :target_id           (:_id d)
                                                            :rel                 "resource"
                                                            :inverse_rel         "revisions"
                                                            :collaboration_roots (util/collab-roots d)}])}
                                :named_links {}}]

             "EquipmentSetup" [{:attributes  {;; v3 <- v2
                                              :device_details (util/parameters :devices)
                                              }

                                :links       {:owner owner-link}

                                :named_links {}}]

             "EpochGroup"     [{:attributes  {;; v3 <- v2
                                              :label               :label
                                              :start               :start
                                              :start_zone          :startZone
                                              :protocol_parameters (util/parameters :protocolParameters)
                                              :device_parameters   (util/parameters :deviceParameters)
                                              }
                                :links       {;; Per link, list of added.
                                              :owner    owner-link
                                              :parent   (fn [d] [{:source_id           (:_id d)
                                                                  :target_id           (:parent d)
                                                                  :rel                 "parent"
                                                                  :inverse_rel         "epoch_groups"
                                                                  :collaboration_roots (util/collab-roots d)}])
                                              :protocol protocol-link}

                                :named_links {}}]

             "Protocol"       [{:attributes  {:name              :name
                                              :protocol_document :protocolDocument
                                              :code_repository   :scmUrl
                                              :code_function     :functionName
                                              :code_revision     :scmRevision}

                                :links       {:owner owner-link}
                                :named_links {}}]

             "Measurement"    [{:attributes  {;; v3 <- v2
                                              :sources :sourceNames
                                              :devices :devices
                                              :label   :name
                                              }

                                :links       {;; Per link, list of added.
                                              :owner owner-link
                                              :epoch (fn [d] [{:source_id           (:_id d)
                                                               :target_id           (:epoch d)
                                                               :rel                 "epoch"
                                                               :inverse_rel         "measurements"
                                                               :collaboration_roots (util/collab-roots d)}])
                                              :data  (fn [d] [{:source_id           (:_id d)
                                                               :target_id           (:data d)
                                                               :rel                 "data"
                                                               :inverse_rel         "containing_entity"
                                                               :collaboration_roots (util/collab-roots d)}])}

                                :named_links {}}]

             "Experiment"     [{:attributes  {;; v3 <- v2
                                              :purpose             :purpose
                                              :start               :start
                                              :start_zone          :startZone
                                              :protocol_parameters (util/parameters :protocolParameters)
                                              :device_parameters   (util/parameters :deviceParameters)
                                              }
                                :links       {;; Per link, list of added.
                                              :owner           owner-link
                                              :equipment_setup (fn [d] (if (:equipmentSetup d) [{:source_id           (:_id d)
                                                                                                 :target_id           (:equipmentSetup d)
                                                                                                 :rel                 "equipment_setup"
                                                                                                 :inverse_rel         "experiments"
                                                                                                 :collaboration_roots (util/collab-roots d)}]
                                                                                               []))
                                              :projects        (fn [d] (map (fn [child] {:source_id           (:_id d)
                                                                                         :target_id           child
                                                                                         :rel                 "projects"
                                                                                         :inverse_rel         "experiments"
                                                                                         :collaboration_roots (util/collab-roots d)}) (:projectIds d)))
                                              :protocol        protocol-link}

                                :named_links {}}]

             "Project"        [{:attributes  {;; v3 <- v2
                                              :purpose    :purpose
                                              :name       :name
                                              :start      :start
                                              :start_zone :startZone
                                              }
                                :links       {;; Per link, list of added.
                                              :owner owner-link}

                                :named_links {
                                              ;; We haven't used write groups in v2
                                              }}]
             })

