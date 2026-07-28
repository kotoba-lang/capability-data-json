(ns kotoba.capability.data.json
  "Importable contract for data/json."
  (:require [kotoba.core.capability-repository :as repository]))

(def manifest
  (repository/repository-manifest "data/json"))
