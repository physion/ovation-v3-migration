(ns migration.test.core
  (:require-macros [speclj.core :refer [describe it should should-not run-specs]])
  (:require [speclj.core]))

(describe "Annotation document conversion"
  (it "should pass"
    (should true)))

(run-specs)
