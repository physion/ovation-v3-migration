(ns migration.test.core-spec
  (:require-macros [speclj.core :refer [describe it should should-not run-specs]])
  (:require [speclj.core]
            [migration.core :as m]))

(describe "Annotation document conversion"
  (it "should pass"
    (should true))
  (it "should fail"
    (should false))
  (it "should convert tag document"
    (should (not (nil? (m/convert-annotation-document "abc"))))))

(run-specs)
