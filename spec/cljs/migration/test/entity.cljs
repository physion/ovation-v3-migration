(ns migration.test.entity-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not should-contain should-not-contain run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [migration.util :as util]
            [clojure.walk :refer [keywordize-keys]]
            [migration.mapping :as mapping]
            [migration.test.fixtures :refer [epoch measurement analysis-record]]))








(describe "Epoch conversion"
          (it "converts Epoch"
              (let [doc (keywordize-keys epoch)]
                (should= {
                          :_id         (:_id doc)
                          :api_version mapping/api-version
                          :type        (:type doc)
                          :attributes  {:start               (:start doc)
                                        :start_zone          (:startZone doc)
                                        :end                 (:end doc)
                                        :end_zone            (:endZone doc)
                                        :protocol_parameters ((util/parameters :protocolParameters) doc)
                                        :device_parameters   ((util/parameters :deviceParameters) doc)}
                          :links       {:_collaboration_roots (:experimentIds doc)}
                          :named_links {:input_sources {"unit1" {:uri (str "ovation://views/links?key=[%22ovation://entities/" (:_id doc) "%22,%22input_sources%22,%22unit1%22]")}}}
                          }
                         (first (m/convert doc (:ownerUuid doc))))))


          (it "does not convert documents that aren't owned"
              (let [doc (keywordize-keys epoch)]
                (should (empty? (m/convert doc "other-id")))))

          (it "has owner relation"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--owner-->" (:ownerUuid doc))
                                 :type        "Relation"
                                 :api_version "3"
                                 :rel         "owner"
                                 :source_id   (:_id doc)
                                 :target_id   (:ownerUuid doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))

          (it "has experiment relation"
              (let [doc (keywordize-keys epoch)]
                (should-contain {:_id         (str (:_id doc) "--experiment-->" (:experiment doc))
                                 :type        "Relation"
                                 :api_version "3"
                                 :rel         "experiment"
                                 :inverse_rel "epochs"
                                 :source_id   (:_id doc)
                                 :target_id   (:experiment doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}} (m/convert doc (:ownerUuid doc))))))


(it "has parent relation"
    (let [doc (keywordize-keys epoch)]
      (should (some #{{:_id         (str (:_id doc) "--parent-->" (:parent doc))
                       :type        "Relation"
                       :api_version "3"
                       :rel         "parent"
                       :inverse_rel "epochs"
                       :source_id   (:_id doc)
                       :target_id   (:parent doc)
                       :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))

(it "has not have protocol relation when protocol nil"
    (let [doc (keywordize-keys epoch)]
      (should (not (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                            :type        "Relation"
                            :api_version "3"
                            :rel         "protocol"
                            :inverse_rel "procedures"
                            :source_id   (:_id doc)
                            :target_id   (:protocol doc)
                            :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc)))))))

(it "has not have protocol relation when protocol not nil"
    (let [doc (keywordize-keys (assoc epoch "protocol" "91047ab3-d4da-471d-9170-37f164a5a027"))]
      (should (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                       :type        "Relation"
                       :api_version "3"
                       :rel         "protocol"
                       :inverse_rel "procedures"
                       :source_id   (:_id doc)
                       :target_id   (:protocol doc)
                       :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))


(it "has input source relations when present"
    (let [doc (keywordize-keys epoch)]
      (should (some #{{:_id         (str (:_id doc) "--input_sources>unit1-->" "00c66b67-1126-4cc0-af05-fd4ad188567f")
                       :type        "Relation"
                       :api_version "3"
                       :rel         "input_sources"
                       :name        "unit1"
                       :inverse_rel "epochs"
                       :source_id   (:_id doc)
                       :target_id   "00c66b67-1126-4cc0-af05-fd4ad188567f"
                       :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))

(it "has output source relations when present"
    (let [doc (assoc (keywordize-keys epoch) :outputSources (:inputSources (keywordize-keys epoch)))]
      (should (some #{{:_id         (str (:_id doc) "--output_sources>unit1-->" "00c66b67-1126-4cc0-af05-fd4ad188567f")
                       :type        "Relation"
                       :api_version "3"
                       :rel         "output_sources"
                       :inverse_rel "producing_procedure"
                       :name        "unit1"
                       :source_id   (:_id doc)
                       :target_id   "00c66b67-1126-4cc0-af05-fd4ad188567f"
                       :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))



(describe "Measurement conversion"
          (it "should have data relation"
              (let [doc (keywordize-keys measurement)]
                (should (some #{{:_id         (str (:_id doc) "--data-->" (:data doc))
                                 :type        "Relation"
                                 :rel         "data"
                                 :api_version "3"
                                 :inverse_rel "containing_entity"
                                 :source_id   (:_id doc)
                                 :target_id   (:data doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))
          )

(describe "Analysis Record conversion"
          (it "should have inputs relation"
              (let [doc (keywordize-keys analysis-record)]
                (should (some #{{:_id         (str (:_id doc) "--inputs>example.xlsx_1-->" "e5e28d4c-0eb5-4f96-a1a0-5d90feec66a2")
                                 :type        "Relation"
                                 :api_version "3"
                                 :rel         "inputs"
                                 :name        "example.xlsx_1"
                                 :inverse_rel "analyses"
                                 :source_id   (:_id doc)
                                 :target_id   "e5e28d4c-0eb5-4f96-a1a0-5d90feec66a2"
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))
          (it "should have outputs relation"
              (let [doc (keywordize-keys analysis-record)]
                (should (some #{{:_id         (str (:_id doc) "--outputs>analysis.mat-->" "939263e7-0fd9-438e-bcfb-7d7e83111fa8")
                                 :type        "Relation"
                                 :api_version "3"
                                 :rel         "outputs"
                                 :name        "analysis.mat"
                                 :inverse_rel "containing_entity"
                                 :source_id   (:_id doc)
                                 :target_id   "939263e7-0fd9-438e-bcfb-7d7e83111fa8"
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc (:ownerUuid doc))))))

          (it "should add annotation record for parent"
              (let [doc (keywordize-keys analysis-record)
                    random-uuid-str (util/random-uuid)
                    docs (m/convert doc (:ownerUuid doc))]
                (with-redefs [util/random-uuid (fn [] random-uuid-str)]
                             (should-contain {:_id             (str "analysis_records_" random-uuid-str)
                                              :type            "Annotation"
                                              :api_version     3
                                              :user            (util/make-entity-uri (:ownerUuid doc))
                                              :entity          (util/make-entity-uri (:parent doc))
                                              :annotation_type "analysis_records"
                                              :annotation      {
                                                                :uri (util/make-entity-uri (:_id doc))
                                                                }
                                              :links           {:_collaboration_roots (util/collab-roots doc)}}
                                             docs)))))


(describe "Trashed entities"
          (it "should migrate trash info"
              (let [trash-info (keywordize-keys {"trashing_user" "1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                                 "trashing_date" "2013-12-12T15:11:03.019-05:00",
                                                 "trash_root"    "39771d61-361e-497c-a55c-4cd3a83d1ac6"})
                    trashed (assoc (keywordize-keys epoch) :trash_info trash-info)
                    expected (keywordize-keys {"trashing_user" "ovation://entities/1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                               "trashing_date" "2013-12-12T15:11:03.019-05:00",
                                               "trash_root"    "ovation://entities/39771d61-361e-497c-a55c-4cd3a83d1ac6"})]

                (should= expected (:trash_info (first (m/convert trashed (:ownerUuid trashed))))))))

(describe "Migrations"
          (it "has all entities"
              (should= #{"Project", "Experiment", "EpochGroup", "Source", "Protocol", "Epoch", "Measurement", "Resource", "AnalysisRecord", "User", "EquipmentSetup"}
                       (set (keys mapping/v2->v3)))))

;
;"trash_info": {
"trashing_user" "1dbbba70-08c7-0131-2b72-22000aa62e2d",
"trashing_date" "2013-12-12T15:11:03.019-05:00",
"trash_root" "39771d61-361e-497c-a55c-4cd3a83d1ac6"
;              ,
