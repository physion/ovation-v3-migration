(ns migration.test.core-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
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
                          "user"            (m/make-entity-uri (:userId doc))
                          "entity"          (m/make-entity-uri (:entityId doc))})
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
                          "user"            (m/make-entity-uri (:userId doc))
                          "entity"          (m/make-entity-uri (:entityId doc))} (m/convert (keywordize-keys (js->clj property-annotation)))))))


(describe "Entity URI creation"
          (it "should prepend ovation://entities/"
              (let [id "123-abc-def-789"]
                (should= (str "ovation://entities/" id) (m/make-entity-uri id)))))
(run-specs)
