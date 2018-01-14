(ns aws-rss-updater.model
  (:require
    [clojure.spec.alpha :as s]
    [aws-rss-updater.s3 :as s3]))

(comment
  {::email "your.email@example.com"
   ::feeds [{:feed/url        ""
             :feed/sent #{""}}]}
  )

(def state-file "aws-rss-updater/state.nippy")

(s/def ::email string?)

(s/def :feed/url string?)
(s/def :feed/sent (s/coll-of string? :kind set?))
(s/def ::feed (s/keys :req [:feed/url :feed/sent]))
(s/def ::feeds (s/coll-of ::feed :kind vector?))

(s/def ::state (s/keys :req [::email ::feeds]))

(defn init
  "Initialize the state."
  [settings]
  (let [s3-client (s3/client (:region settings))
        bucket (:bucket settings)]
    (s3/create-bucket s3-client bucket)
    (s3/put-object s3-client bucket state-file {})))

(defn get-state
  [settings]
  (let [s3-client (s3/client (:region settings))]
    (s3/get-object s3-client (:bucket settings) state-file)))

(defn mark-post-as-sent
  [settings feed-url post-url]
  (let [s3-client (s3/client (:region settings))]
    (s3/update-object s3-client (:bucket settings) state-file
                      (fn [state]
                        (update state ::feeds
                                (fn [feeds]
                                  (mapv (fn [{:keys [feed/url] :as feed}]
                                          (if (= url feed-url)
                                            (update feed :feed/sent conj post-url)
                                            feed))
                                        feeds)))))))