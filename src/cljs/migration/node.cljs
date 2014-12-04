(ns migration.node
  (:require [migration.core :as core]
            [clojure.walk :refer [keywordize-keys stringify-keys]]
            [cljs.nodejs :as nodejs]))

(defn ^:export migrate
  [doc]
  (clj->js (stringify-keys (core/convert (keywordize-keys (js->clj doc))))))

(defn ^:export -main [] nil)

(nodejs/enable-util-print!)
(set! *main-cli-fn* -main)
