(ns migration.test.fixtures)

(def parent-source-with-children {
                                  "_id"             "7b266dc2-d0f0-4cf5-bfcc-656db98a6693",
                                  "_rev"            "3-dc7ea09bd51aabbaee8799bee06d27cf",
                                  "type"            "Source",
                                  "ownerUuid"       "60f860c0-5ed4-0132-eca8-22000a0b96d9",
                                  "writeGroupIds"   [],
                                  "resources"       [],
                                  "version"         "2.1.29",
                                  "label"           "source2",
                                  "identifier"      "source2",
                                  "childrenSources" [
                                                     "967731c7-bd18-4b8d-8c89-e8536aecdac2",
                                                     "6fa3dad4-9f97-4d4a-aade-81fe67a8dfc0"
                                                     ],
                                  "experimentIds"   [
                                                     "a1abbed9-7078-4be9-895e-7d0c4a9d9ecd"
                                                     ],
                                  "projectIds"      [
                                                     "b3ca6710-7d94-401f-aa0b-ca34d4050d51"
                                                     ],
                                  "entity"          true
                                  })

(def parent-source {
                    "_id"             "573b671d-f4a0-4aa7-9588-4f65868b7ec9",
                    "_rev"            "2-7add2d333af99e72f8221abd8ff7a4e5",
                    "type"            "Source",
                    "ownerUuid"       "60f860c0-5ed4-0132-eca8-22000a0b96d9",
                    "writeGroupIds"   [],
                    "resources"       [],
                    "version"         "2.1.29",
                    "label"           "source1",
                    "identifier"      "source1",
                    "childrenSources" [],
                    "experimentIds"   [
                                       "a1abbed9-7078-4be9-895e-7d0c4a9d9ecd"
                                       ],
                    "projectIds"      [
                                       "b3ca6710-7d94-401f-aa0b-ca34d4050d51"
                                       ],
                    "entity"          true
                    })

(def child-source {
                   "_id"             "6fa3dad4-9f97-4d4a-aade-81fe67a8dfc0",
                   "_rev"            "3-df1dcccb9abf5d20c866fd9e65f5ef17",
                   "type"            "Source",
                   "ownerUuid"       "60f860c0-5ed4-0132-eca8-22000a0b96d9",
                   "writeGroupIds"   [],
                   "resources"       [],
                   "version"         "2.1.29",
                   "label"           "child-1",
                   "identifier"      "source2-child-1",
                   "childrenSources" [],
                   "experimentIds"   [],
                   "projectIds"      [],
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

(def measurement {
                  "_id"           "bc29eaba-de7b-466e-bdbb-df2d3e7195d8",
                  "_rev"          "1-53dc88fb43f18cab27dcb2028765a473",
                  "type"          "Measurement",
                  "ownerUuid"     "60f860c0-5ed4-0132-eca8-22000a0b96d9",
                  "writeGroupIds" [],
                  "resources"     [],
                  "version"       "2.1.29",
                  "name"          "060810Bc2.jpg",
                  "epoch"         "5f6178d3-9d99-4bf4-9e61-cb19cd2e1d43",
                  "sourceNames"   [],
                  "devices"       [],
                  "data"          "43bb6579-653b-481a-a796-8720069a4983",
                  "experimentIds" [
                                   "cc0ffad5-f6f0-439b-a1c6-8ac6aeef794a"
                                   ],
                  "projectIds"    [
                                   "b3ca6710-7d94-401f-aa0b-ca34d4050d51"
                                   ],
                  "entity"        true
                  })
