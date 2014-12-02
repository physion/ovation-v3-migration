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
                                                                   :target_id           (:parent d)
                                                                   :rel                 "parent"
                                                                   :collaboration_roots (:experimentIds d)}])
                                             :protocol   (fn [d] (if (:protocol d) [{:source_id           (:_id d)
                                                                                     :target_id           (:protocol d)
                                                                                     :rel                 "protocol"
                                                                                     :inverse_rel         "procedures"
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
                                                                                   :inverse_rel         "procedures"
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

             "EquipmentSetup" {:attributes  {;; v3 <- v2
                                             :device_details (util/parameters :devices)
                                             }

                               :links       {:owner owner-link}

                               :named_links {}}

             "EpochGroup"     {:attributes  {;; v3 <- v2
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
                                                                 :collaboration_roots (:experimentIds d)}])
                                             :protocol (fn [d] (if (:protocol d) [{:source_id           (:_id d)
                                                                                   :target_id           (:protocol d)
                                                                                   :rel                 "protocol"
                                                                                   :inverse_rel         "procedures"
                                                                                   :collaboration_roots (:experimentIds d)}] []))}

                               :named_links {}}

             "Protocol" {:attributes {:name :name
                                      :protocol_document :protocolDocument
                                      :code_repository :scmUrl
                                      :code_function :functionName
                                      :code_revision :scmRevision}

                         :links {:owner    owner-link}
                         :named_links {}}

             ;"Measurement" {}
             ;"Experiment" {}
             ;"Project" {}
             })


;public static final String NAME = "name";
;public static final String PROTOCOL_DOCUMENT = "protocol_document";
;
;public static final String CODE_FUNCTION = "code_function";
;public static final String CODE_SCM_URL = "code_repository";
;public static final String CODE_REVISION = "code_revision";

;
;;{
;"_id": "11b13d33-9fad-4612-ba0d-5d5a5dfa85b6",
;"_rev": "28-6f87c10d3f5bc4ca1bc81b3e587ec60a",
;"name": "fenton-analysis-protocol-demo",
;"entity": true,
;"scmUrl": "https://github.com/FentonLab/analysis-code",
;"version": "2.0-beta6",
;"writeGroupIds": [],
;"protocolDocument": "Automated analysis",
;"experimentIds": [
;                  "aeb7ad18-e491-4438-96a3-2b8a5ff60399"
;                  ],
;"projectIds": [
;               "fabd0d84-ccb1-4dde-a4c8-bd0915d25bbf"
;               ],
;"scmRevision": "TBD",
;"type": "Protocol",
;"ownerUuid": "d4ceca40-83cb-0130-3b75-22000aab13b3",
;"resources": [],
;"functionName": "analysis_entry_function"
;  }
