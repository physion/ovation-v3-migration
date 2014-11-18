(ns migration.core
  (:require [clojure.walk]))

(defn make-entity-uri
  "Makes an ovation:// entity URI from a UUID string"
  [id]
  (str "ovation://entities/" id))


(defn convert-keyword-document
  "Converts an annotation document"
  [doc-v2]

  (let [doc (clojure.walk/keywordize-keys doc-v2)]
    {
     "_id" (str "keywords_" (:_id doc))
     "_rev" (:_rev doc)
     "annotation" {"tag" (:tag doc)}
     }))
