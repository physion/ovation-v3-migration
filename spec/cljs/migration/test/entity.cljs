(ns migration.test.entity-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [migration.util :as util]
            [clojure.walk :refer [keywordize-keys]]
            [migration.mapping :as mapping]))





(def epoch {
            "_id"                "01ad0c48-39df-414d-ae20-615d39e393a9",
            "_rev"               "2-5cb8cff041e971b93bb5d38ecb2e0afd",
            "type"               "Epoch",
            "ownerUuid"          "8802f3e0-0b98-0132-451b-22000a0b96d9",
            "writeGroupIds"      [],
            "resources"          [],
            "version"            "2.1.26",
            "start"              "2014-09-14T14:11:03.885-04:00",
            "end"                "2014-09-14T14:11:08.885-04:00",
            "startZone"          "America/New_York",
            "endZone"            "America/New_York",
            "experiment"         "91047ab3-d4da-471d-9170-37f164a5a027",
            "parent"             "91047ab3-d4da-471d-9170-37f164a5a027",
            "inputSources"       [
                                  {
                                   "key"   "unit1",
                                   "value" "00c66b67-1126-4cc0-af05-fd4ad188567f"}
                                  ],
            "outputSources"      [],
            "protocolParameters" [{"key"   "injectionDate",
                                   "value" "20130709"},
                                  {"key"   "infectionCoordinates",
                                   "value" "[[2.500, -1.500, .500], [2.500, -1.500, .8]]"}],
            "deviceParameters"   [{"key"   "version",
                                   "value" "20130709"},
                                  ],
            "projectIds"         [
                                  "9c7066a8-4f22-42d9-82fe-43fec312fab2"
                                  ],
            "experimentIds"      [
                                  "91047ab3-d4da-471d-9170-37f164a5a027"
                                  ],
            "entity"             true})


(describe "Epoch conversion"
          (it "converts Epoch"
              (let [doc (keywordize-keys epoch)]
                (should= {
                          :_id         (:_id doc)
                          :_rev        (:_rev doc)
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
                                 :inverse_rel nil
                                 :source_id   (:_id doc)
                                 :target_id   (:experiment doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))


          (it "has parent relation"
              (let [doc (keywordize-keys epoch)]
                (should (some #{{:_id         (str (:_id doc) "--parent-->" (:parent doc))
                                 :type        "Relation"
                                 :rel         "parent"
                                 :inverse_rel nil
                                 :source_id   (:_id doc)
                                 :target_id   (:parent doc)
                                 :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc)))))

          (it "has not have protocol relation when protocol nil"
              (let [doc (keywordize-keys epoch)]
                (should (not (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                                      :type        "Relation"
                                      :rel         "protocol"
                                      :inverse_rel nil
                                      :source_id   (:_id doc)
                                      :target_id   (:protocol doc)
                                      :links       {:_collaboration_roots (:experimentIds doc)}}} (m/convert doc))))))

          (it "has not have protocol relation when protocol not nil"
              (let [doc (keywordize-keys (assoc epoch "protocol" "91047ab3-d4da-471d-9170-37f164a5a027"))]
                (should (some #{{:_id         (str (:_id doc) "--protocol-->" (:protocol doc))
                                 :type        "Relation"
                                 :rel         "protocol"
                                 :inverse_rel nil
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

(describe "Migrations"
          (it "has all entities"
              (should= #{"Project", "Experiment", "EpochGroup", "Source", "Protocol", "Epoch", "Measurement", "Resource", "AnalysisRecord", "User", "EquipmentSetup"}
                       (keys mapping/v2->v3))))
