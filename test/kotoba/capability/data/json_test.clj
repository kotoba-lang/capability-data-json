(ns kotoba.capability.data.json-test
  (:require [clojure.test :refer [deftest is]]
            [kotoba.capability.data.json :as capability]
            [kotoba.core.capability-repository :as repository]))

(deftest manifest-conforms
  (is (= [] (repository/validate-manifest capability/manifest))))
