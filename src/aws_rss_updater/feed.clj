(ns aws-rss-updater.feed
  (:require
    [clojure.set :as sets]
    [org.httpkit.client :as http]
    [feedparser-clj.core :as parser])
  (:import (com.sun.syndication.io XmlReader)))

(defn parse-feed
  "Returns the parsed feed. Issues a conditional GET request. Returns `nil` if
  the given feed has not changed since last requested."
  [url {:keys [etag]}]
  (let [{:keys [status body headers]}
        @(http/get url
                   {:as      :stream
                    :headers (cond-> {}
                                     etag (assoc "If-None-Match" etag))})]
    (when (= status 200)
      (let [last-modified (:last-modified headers)
            etag (:etag headers)]
        (cond-> {:feed (#'parser/parse-internal (XmlReader. body))}
                last-modified (assoc :last-modified last-modified)
                etag (assoc :etag etag))))))

(defn post-information
  [parsed-feed]
  (map #(select-keys % [:uri :title]) (get-in parsed-feed [:feed :entries])))

(defn feed-info
  "Returns a map of `:posts`, `:etag`, and `:last-modified` for the feed located
  at `url`."
  [url opts]
  (let [parsed (parse-feed url opts)
        posts (post-information parsed)]
    (merge
      (select-keys parsed [:last-modified :etag])
      {:posts posts})))

(defn posts-to-send
  "Returns a set of maps of posts to send."
  [base-url base-url-headers urls-sent]
  (let [info (feed-info base-url base-url-headers)
        feed-uris (into #{} (map :uri) (:posts info))
        new-uris (sets/difference feed-uris urls-sent)]
    (filter (comp (partial contains? new-uris) :uri) (:posts info))))