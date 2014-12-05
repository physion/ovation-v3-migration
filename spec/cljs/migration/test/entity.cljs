(ns migration.test.entity-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [migration.util :as util]
            [clojure.walk :refer [keywordize-keys]]
            [migration.mapping :as mapping]
            [migration.test.fixtures :refer [epoch]]))








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
                          }
                         (first (m/convert doc)))))

          (it "has owner relation"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--owner-->" (:ownerUuid doc))
                                 :type        "Relation"
                                 :rel         "owner"
                                 :inverse_rel nil
                                 :source_id   (:_id doc)
                                 :target_id   (:ownerUuid doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))

          (it "has experiment relation"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--experiment-->" (:experiment doc))
                                 :type        "Relation"
                                 :rel         "experiment"
                                 :inverse_rel "epochs"
                                 :source_id   (:_id doc)
                                 :target_id   (:experiment doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))


          (it "has parent relation"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--parent-->" (:parent doc))
                                 :type        "Relation"
                                 :rel         "parent"
                                 :inverse_rel "epochs"
                                 :source_id   (:_id doc)
                                 :target_id   (:parent doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))

          (it "has not have protocol relation when protocol nil"
              (let [doc (keywordize-keys epoch)]
                (should (not (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                                      :type        "Relation"
                                      :rel         "protocol"
                                      :inverse_rel "procedures"
                                      :source_id   (:_id doc)
                                      :target_id   (:protocol doc)
                                      :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc))))))

          (it "has not have protocol relation when protocol not nil"
              (let [doc (keywordize-keys (assoc epoch "protocol" "91047ab3-d4da-471d-9170-37f164a5a027"))]
                (should (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                                 :type        "Relation"
                                 :rel         "protocol"
                                 :inverse_rel "procedures"
                                 :source_id   (:_id doc)
                                 :target_id   (:protocol doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))


          (it "has input source relations when present"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--input_sources>unit1-->" "00c66b67-1126-4cc0-af05-fd4ad188567f")
                                 :type        "Relation"
                                 :rel         "input_sources"
                                 :name        "unit1"
                                 :inverse_rel nil
                                 :source_id   (:_id doc)
                                 :target_id   "00c66b67-1126-4cc0-af05-fd4ad188567f"
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))

          (it "has output source relations when present"
              (let [doc (assoc (keywordize-keys epoch) :outputSources (:inputSources (keywordize-keys epoch)))]
                (should (some #{{:_id         (str (:_id doc) "--output_sources>unit1-->" "00c66b67-1126-4cc0-af05-fd4ad188567f")
                                 :type        "Relation"
                                 :rel         "output_sources"
                                 :name        "unit1"
                                 :inverse_rel nil
                                 :source_id   (:_id doc)
                                 :target_id   "00c66b67-1126-4cc0-af05-fd4ad188567f"
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))
          )

(describe "Source converstion"
          (it "should mark root sources as root"
              (should false))
          (it "should not mark child sources as root"
              (should false)))

(describe "Trashed entities"
          (it "should migrate trash info"
              (let [trash-info (keywordize-keys {"trashing_user" "1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                                 "trashing_date" "2013-12-12T15:11:03.019-05:00",
                                                 "trash_root"    "39771d61-361e-497c-a55c-4cd3a83d1ac6"})
                    trashed (assoc (keywordize-keys epoch) :trash_info trash-info)
                    expected (keywordize-keys {"trashing_user" "ovation://entities/1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                               "trashing_date" "2013-12-12T15:11:03.019-05:00",
                                               "trash_root"    "ovation://entities/39771d61-361e-497c-a55c-4cd3a83d1ac6"})]

                (should= expected (:trash_info (first (m/convert trashed)))))))

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
