(ns migration.test.core-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]
            [clojure.walk]))

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

(describe "Annotation document conversion"
  (it "should convert tag document"
    (should= (let [doc (clojure.walk/keywordize-keys (js->clj tag-annotation))
                   entity-id (:entityId doc)]
               {"_id" (str "keywords_" (:_id doc))           ;;TODO
                "_rev" (:_rev doc)
                "annotation" {"tag" (:tag doc)}
                "links" {"_collaboration_roots" (:experimentIds doc)}
                "type" "Annotation"
                "user" (m/make-entity-uri (:userId doc))
                "entity" (m/make-entity-uri (:entityId doc))})
      (m/convert-keyword-document (js->clj tag-annotation)))))

(run-specs)
