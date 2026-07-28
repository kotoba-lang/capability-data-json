(ns kotoba.capability.data.json.provider
  "JVM reference host provider for actor:host fields
  \"json_encode\" and \"json_extract_field\".

  Pure surfaces:
  - encode flat key\\tvalue / LF pairs as a JSON object of string→string
  - extract a string-valued field via bounded `\"field\":\"value\"` scan
  ABI matches capability_contract.edn; memory wiring is the embedder's job."
  (:import [java.nio.charset StandardCharsets]))

(defn parse-pairs
  [^String wire]
  (into []
        (keep (fn [line]
                (when-not (or (nil? line) (zero? (count line)))
                  (let [tab (.indexOf line (int \tab))]
                    (when (neg? tab)
                      (throw (ex-info "pair missing tab" {:line line})))
                    [(.substring line 0 tab) (.substring line (inc tab))])))
              (.split wire "\n" -1))))

(defn- json-escape
  [^String s]
  (let [sb (StringBuilder.)]
    (doseq [ch s]
      (case ch
        \" (.append sb "\\\"")
        \\ (.append sb "\\\\")
        \newline (.append sb "\\n")
        \return (.append sb "\\r")
        \tab (.append sb "\\t")
        (.append sb ch)))
    (str sb)))

(defn encode-object
  "JSON object of string keys/values (insertion order)."
  [pairs]
  (let [sb (StringBuilder.)]
    (.append sb \{)
    (doseq [[idx [k v]] (map-indexed vector pairs)]
      (when (pos? idx) (.append sb \,))
      (.append sb \")
      (.append sb (json-escape (str k)))
      (.append sb "\":\"")
      (.append sb (json-escape (str v)))
      (.append sb \"))
    (.append sb \})
    (str sb)))

(defn encode-pairs-wire
  [^String wire]
  (encode-object (parse-pairs wire)))

(defn- skip-ws
  [^String s i]
  (loop [j i]
    (if (>= j (count s))
      j
      (let [c (.charAt s j)]
        (if (or (= c \space) (= c \tab) (= c \newline) (= c \return))
          (recur (inc j))
          j)))))

(defn- unescape-json-string
  "Read a JSON string body starting at index `start` (first char after opening
  quote). Returns [value end-index-after-closing-quote] or nil."
  [^String json start]
  (let [sb (StringBuilder.)
        n (count json)]
    (loop [i start]
      (when (< i n)
        (let [c (.charAt json i)]
          (cond
            (= c \")
            [(str sb) (inc i)]

            (= c \\)
            (when (< (inc i) n)
              (let [e (.charAt json (inc i))
                    ch (case e
                         \" \"
                         \\ \\
                         \n \newline
                         \r \return
                         \t \tab
                         \/ \/
                         nil)]
                (when ch
                  (.append sb ch)
                  (recur (+ i 2)))))

            :else
            (do (.append sb c)
                (recur (inc i)))))))))

(defn extract-field
  "Bounded scan for `\"field\"` then optional whitespace, `:`, optional
  whitespace, `\"value\"`. Returns the unescaped value string or nil."
  [^String json ^String field]
  (let [needle (str "\"" field "\"")
        start (.indexOf json needle)]
    (when-not (neg? start)
      (let [j (skip-ws json (+ start (count needle)))]
        (when (and (< j (count json)) (= (.charAt json j) \:))
          (let [k (skip-ws json (inc j))]
            (when (and (< k (count json)) (= (.charAt json k) \"))
              (first (unescape-json-string json (inc k))))))))))

(defn host-exports
  "Both actor:host fields for data/json."
  []
  [{:module "kotoba"
    :field "json_encode"
    :params [:i32 :i32 :i32 :i32]
    :result :i32
    :fn encode-pairs-wire}
   {:module "kotoba"
    :field "json_extract_field"
    :params [:i32 :i32 :i32 :i32 :i32 :i32]
    :result :i32
    :fn extract-field}])
