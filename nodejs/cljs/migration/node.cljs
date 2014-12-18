(ns migration.node
  (:require [migration.core :as core]
            [clojure.walk :refer [keywordize-keys stringify-keys]]
            [cljs.nodejs :as nodejs]))

(defn ^:export migrate
  [doc user-id]
  (let [docs (core/convert (keywordize-keys (js->clj doc)) user-id)]
    (clj->js (into [] (map (fn [doc] (clj->js (stringify-keys doc))) docs)))))

(defn ^:export -main [] nil)

(nodejs/enable-util-print!)
(set! *main-cli-fn* -main)
