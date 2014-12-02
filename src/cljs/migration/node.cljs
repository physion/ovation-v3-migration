(ns migration.node
  (:require [migration.core :as core]
            [cljs.nodejs :as nodejs]))

(defn ^:export migrate
  [doc]
  (print doc)
  (core/convert doc))

(nodejs/enable-util-print!)
(set! *main-cli-fn* migrate)
