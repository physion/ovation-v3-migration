(ns migration.test.core-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [migration.util :as util]
            [clojure.walk :refer [keywordize-keys]]))

(def tag-annotation (clj->js {"_id"           "c3ed9a1e-510f-4f1a-a953-3af077584714",
                              "_rev"          "1-ff88aa8ff4eaeb4fd01a9ee42a4e18e1",
                              "type"          "TagAnnotation",
                              "userId"        "8dc20340-36cf-0132-f8c3-22000ae9209a",
                              "tag"           "BRCA",
                              "entityId"      "406efff7-e47d-4894-a307-9464fe6e535d",
                              "writeGroupIds" [],
                              "experimentIds" ["77dd245a-cf9a-4524-9002-6217e5392e63"],
                              "projectIds"    ["b4053f54-0ba8-4eaa-9c05-6dcfd8e94324"],
                              "version"       "2.1.27",
                              "entity"        false,
                              "ownerUuid"     "8dc20340-36cf-0132-f8c3-22000ae9209a"}))

(def property-annotation (clj->js {"_id"           "091d72f4-8be0-4ace-b983-5c6a0f67906d",
                                   "_rev"          "1-d7427fda0c97ea39a1b464cdb5680199",
                                   "type"          "PropertyAnnotation",
                                   "userId"        "d4ceca40-83cb-0130-3b75-22000aab13b3",
                                   "key"           "__symphony__source__id__",
                                   "value"         "af791a92-f854-42a4-87d4-e4c24882a9d9",
                                   "entityId"      "00ae48b8-ccae-4006-9c28-285a888f358f",
                                   "writeGroupIds" [],
                                   "experimentIds" [
                                                    "e698360f-28c4-4018-9ea0-20c1e73eaf2f"
                                                    ],
                                   "projectIds"    [
                                                    "2ebffdf4-42c5-472f-aee7-9e778fcef260"
                                                    ],
                                   "version"       "2.0.0-beta6",
                                   "entity"        false,
                                   "ownerUuid"     "d4ceca40-83cb-0130-3b75-22000aab13b3"}))


(def note-annotation (clj->js {
                               "_id"           "5540a9cf-3397-4d0e-9ad5-11f01444cdaf",
                               "_rev"          "1-2142b116ca62ed79f849968427e2b424",
                               "type"          "NoteAnnotation",
                               "userId"        "8dc20340-36cf-0132-f8c3-22000ae9209a",
                               "text"          "@suzie what do think of this?",
                               "timestamp"     "2014-11-01T20:56:11.824Z",
                               "entityId"      "3338d385-acb1-4dec-9bbf-b4ee0245d34e",
                               "timestampZone" "UTC",
                               "writeGroupIds" [],
                               "experimentIds" [
                                                "35dd6dd9-0203-427d-a8be-57f3f7e2580b"
                                                ],
                               "projectIds"    [
                                                "b4053f54-0ba8-4eaa-9c05-6dcfd8e94324"
                                                ],
                               "version"       "2.1.29",
                               "entity"        false,
                               "ownerUuid"     "8dc20340-36cf-0132-f8c3-22000ae9209a"}))

(def timeline-annotation (clj->js {"_id"           "08473fa7-35e4-4283-acd5-3696056eb02b",
                                   "_rev"          "1-8b11f67404c59f3425a1be56a142d2b0",
                                   "type"          "TimelineAnnotation",
                                   "userId"        "1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                   "name"          "Wheel Run CW",
                                   "notes"         "last-wheel-run-cw  lfpStartIndex: 4846 lfpEndIndex: 21562",
                                   "start"         "2012-08-19T04:51:03.876-04:00",
                                   "end"           "2012-08-19T04:51:17.249-04:00",
                                   "entityId"      "00b4c153-3f6c-45f8-beb3-4c6496fbb871",
                                   "startZone"     "America/New_York",
                                   "endZone"       "America/New_York",
                                   "writeGroupIds" [],
                                   "experimentIds" [
                                                    "39771d61-361e-497c-a55c-4cd3a83d1ac6"
                                                    ],
                                   "projectIds"    [
                                                    "5e4300e9-dd4b-4074-ae59-fda73ae84512"
                                                    ],
                                   "version"       "2.1.0",
                                   "trash_info"    {
                                                    "trashing_user" "1dbbba70-08c7-0131-2b72-22000aa62e2d",
                                                    "trashing_date" "2013-12-12T15:11:02.145-05:00",
                                                    "trash_root"    "39771d61-361e-497c-a55c-4cd3a83d1ac6"}
                                   "entity"        false,
                                   "ownerUuid"     "1dbbba70-08c7-0131-2b72-22000aa62e2d"
                                   }))




(describe "Annotation document conversion"
          (it "should convert keyword document"
              ;{
              ;"_id": "keywords_e6b69488-ccbb-4ddf-80da-95db43dc9d4e",
              ;"_rev": "1-4ffa8156db6c0f041c17b974dcc5bf2d",
              ;"annotation": {
              ;               "tag": "find"
              ;                      ,
              ;"annotation_type": "keywords",
              ;"links": {
              ;          "_collaboration_roots": [
              ;                                   "678544f2-0a3b-40a1-8b7e-3c3febb9c7f4"
              ;                                   ]
              ;                                  ,
              ;"type": "Annotation",
              ;"user": "ovation://entities/e7bf5920-4b3d-0132-5d38-22000a0b96d9",
              ;"entity": "ovation://entities/49754fa6-df69-452e-8808-24e7d12c5bf6"
              (should= (let [doc (keywordize-keys (js->clj tag-annotation))]
                         {"_id"             (str "keywords_" (:_id doc))
                          "_rev"            (:_rev doc)
                          "annotation"      {"tag" (:tag doc)}
                          "links"           {"_collaboration_roots" (:experimentIds doc)}
                          "type"            "Annotation"
                          "annotation_type" "keywords"
                          "api_version"     "3"
                          "user"            (util/make-entity-uri (:userId doc))
                          "entity"          (util/make-entity-uri (:entityId doc))})
                       (m/convert (keywordize-keys (js->clj tag-annotation)))))

          (it "should convert property document"
              (let [doc (keywordize-keys (js->clj property-annotation))]
                ; {
                ;"_id": "properties_53a77418-3250-498b-bdae-c93a28f2a094",
                ;"_rev": "1-167671d364ccd59ccebcc8a47e514dcc",
                ;"annotation": {
                ;               "key": "number",
                ;"value": 2
                ;         ,
                ;"annotation_type": "properties",
                ;"entity": "ovation://entities/0adddfad-3542-46f4-8af4-9a8231eb271f",
                ;"links": {
                ;          "_collaboration_roots": [
                ;                                   "cb7ccbbf-045d-4580-a1ac-f08ee7452931"
                ;                                   ]
                ;                                  ,
                ;"user": "ovation://entities/eae74740-5a98-0131-372a-22000a977b96"

                (should= {"_id"             (str "properties_" (:_id doc))
                          "_rev"            (:_rev doc)
                          "annotation"      {"key"   (:key doc)
                                             "value" (:value doc)}
                          "links"           {"_collaboration_roots" (:experimentIds doc)}
                          "type"            "Annotation"
                          "annotation_type" "properties"
                          "api_version"     "3"
                          "user"            (util/make-entity-uri (:userId doc))
                          "entity"          (util/make-entity-uri (:entityId doc))}
                         (m/convert (keywordize-keys (js->clj property-annotation))))))

          (it "should convert note annotation"
              (let [doc (keywordize-keys (js->clj note-annotation))]
                (should= {"_id"             (str "notes_" (:_id doc))
                          "_rev"            (:_rev doc)
                          "annotation"      {"text"       (:text doc)
                                             "time_stamp" (:timestamp doc)}
                          "links"           {"_collaboration_roots" (:experimentIds doc)}
                          "type"            "Annotation"
                          "annotation_type" "notes"
                          "api_version"     "3"
                          "user"            (util/make-entity-uri (:userId doc))
                          "entity"          (util/make-entity-uri (:entityId doc))}
                         (m/convert (keywordize-keys (js->clj note-annotation))))))

          (it "should convert timeline annotaiton"
              (let [doc (keywordize-keys (js->clj timeline-annotation))]
                (should= {"_id"             (str "timeline_events_" (:_id doc))
                          "_rev"            (:_rev doc)
                          "annotation"      {"name"  (:name doc)
                                             "notes" (:notes doc)
                                             "start" (:start doc)
                                             "end"   (:end doc)}
                          "links"           {"_collaboration_roots" (:experimentIds doc)}
                          "type"            "Annotation"
                          "annotation_type" "timeline_events"
                          "api_version"     "3"
                          "user"            (util/make-entity-uri (:userId doc))
                          "entity"          (util/make-entity-uri (:entityId doc))}
                         (m/convert (keywordize-keys (js->clj timeline-annotation))))))
          )


(describe "Entity URI creation"
          (it "should prepend ovation://entities/"
              (let [id "123-abc-def-789"]
                (should= (str "ovation://entities/" id) (util/make-entity-uri id)))))
(run-specs)
