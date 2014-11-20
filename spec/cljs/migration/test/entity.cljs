(ns migration.test.entity-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [migration.util :as util]
            [clojure.walk :refer [keywordize-keys]]))

(def root-source {
                  "_id"             "c88e43a9-6470-4700-a88c-e9de94a1e93f"
                  "_rev"            "8-53a08031b1737ee80f781ab04b08cfde"
                  "type"            "Source"
                  "ownerUuid"       "8dc20340-36cf-0132-f8c3-22000ae9209a"
                  "writeGroupIds"   []
                  "resources"       []
                  "version"         "2.1.27"
                  "label"           "TCGA-A1-A0SB-01A-11R-A144-07"
                  "identifier"      "a2405d64-34eb-4915-abf7-8530151d5cb0"
                  "childrenSources" []
                  "experimentIds"   []
                  "projectIds"      []
                  "trash_info"      {
                                     "trashing_date" "2014-10-16T12:33:52.534-04:00"
                                     "trashing_user" "8dc20340-36cf-0132-f8c3-22000ae9209a"
                                     "trash_root"    "c88e43a9-6470-4700-a88c-e9de94a1e93f"
                                     "entity"        true
                                     }
                  })


(def child-source {
                   "_id"             "848e4b5a-c6e2-4182-b553-a3a719c59d61"
                   "_rev"            "14-94dbc82a98a9356f7aafc137146ff9d9"
                   "type"            "Source"
                   "ownerUuid"       "8dc20340-36cf-0132-f8c3-22000ae9209a"
                   "writeGroupIds"   []
                   "resources"       []
                   "version"         "2.1.27"
                   "label"           "TCGA-A1-A0SB"
                   "identifier"      "0045349c-69d9-4306-a403-c9c1fa836644"
                   "childrenSources" [
                                      "6333eadc-efc6-41fc-9d26-0e9f2dae5195"
                                      ]
                   "experimentIds"   [
                                      "77dd245a-cf9a-4524-9002-6217e5392e63"
                                      "d2b989a6-1d20-40c9-92cc-40f3b036d6b0"
                                      ]
                   "projectIds"      [
                                      "b4053f54-0ba8-4eaa-9c05-6dcfd8e94324"
                                      ]
                   "entity"          true
                   })

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

(def project {
              "_id"           "775df4ec-badf-492f-96ea-61986d9e8cd9",
              "_rev"          "2-6d1074ea1208cba174f9722059a91e91",
              "type"          "Project",
              "ownerUuid"     "f3b8b400-14e3-0132-451e-22000a0b96d9",
              "writeGroupIds" [],
              "resources"     [],
              "version"       "2.1.26",
              "name"          "Simon's project",
              "purpose"       "This dataset comprises calcium imaging data from vibrissal S1 in the mice performing a pole \nlocalization task.",
              "start"         "2014-10-08T00:00:00.000-04:00",
              "startZone"     "America/New_York",
              "experimentIds" [],
              "projectIds"    [
                               "775df4ec-badf-492f-96ea-61986d9e8cd9"
                               ],
              "entity"        true
              })



(describe "Base Entity conversion"
          (it "converts base attributes and links"
              (let [doc (keywordize-keys child-source)]
                (should= {"_id"         (:_id doc)
                          "_rev"        (:_rev doc)
                          "type"        (:type doc)
                          "api_version" "3"
                          "links"       {util/collaboration-roots (:experimentIds doc)}
                          "owner"       (util/make-entity-uri (:ownerUuid doc))}
                         (m/convert-base doc)))))

(describe "Timeline element conversion"
          (it "should convert start/end timeline components"
              (let [doc (keywordize-keys epoch)
                    base (m/convert-base doc)]
                (should= (merge-with merge base {"attributes" {"start"      (:start doc)
                                                               "start_zone" (:startZone doc)
                                                               "end"        (:end doc)
                                                               "end_zone"   (:endZone doc)}})
                         (m/add-timeline-element doc base))))
          (it "should convert start-only timeline components"
              (let [doc (keywordize-keys project)
                    base (m/convert-base doc)]
                (should= (merge-with merge base {"attributes" {"start"      (:start doc)
                                                               "start_zone" (:startZone doc)}})
                         (m/add-timeline-element doc base)))))

(describe "Procedure element conversion"
          (it "should convert protocol and device parameters"
              (let [doc (keywordize-keys epoch)]
                (should= {"attributes" {"protocol_parameters" (util/map-from-key-value-map-seq (:protocolParameters doc))
                                        "device_parameters" (util/map-from-key-value-map-seq (:deviceParameters doc))}}
                         (m/add-procedure-element doc {}))))
          (it "should allow have empty parameters"
              (let [doc (keywordize-keys epoch)
                    no-params (assoc doc :protocolParameters [] :deviceParameters [])]
                (should= {"attributes" {"protocol_parameters" {}
                                        "device_parameters" {}}}
                         (m/add-procedure-element no-params {}))))
          (it "should add protocol link"))


(describe "Key-value entry seq to map"
          (it "should make a map from key-value record"
              (should= {"key1" "value1"
                        "key2" "value2"}
                       (util/map-from-key-value-map-seq '({:key "key1" :value "value1"} {:key "key2" :value "value2"})))))


(run-specs)
