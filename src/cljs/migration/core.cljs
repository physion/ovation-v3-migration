(ns migration.core
  (:require [clojure.walk]
            [migration.annotation :as annotation]))



(defmulti convert :entity)
(defmethod convert false
  [annotation-doc]
  (annotation/convert-annotation annotation-doc))
