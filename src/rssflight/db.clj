(ns rssflight.db
  (:require [taoensso.faraday :as faraday]))

(def table :rss-table)
(def ddb-opts {:endpoint "http://localhost:8000"})

(defn feed-attribute
  [ddb feed-url attr]
  (get
    (faraday/get-item ddb table
                      {:url feed-url}
                      {:attrs [attr]})
    attr))

(defn create-table
  [ddb]
  (faraday/create-table ddb table
                        [:url :s]
                        {:throughput {:read 1 :write 1}
                         :block?     true}))

(defn recreate-table
  [ddb]
  (faraday/delete-table ddb table)
  (create-table ddb))

(defn subscribe-feed
  [ddb feed-url email]
  (faraday/update-item ddb table
                       {:url feed-url}
                       {:update-expr    "ADD emails :email"
                        :expr-attr-vals {":email" #{email}}
                        :return         :all-new}))

(defn unsubscribe-feed
  [ddb feed-url email]
  (faraday/update-item ddb table
                       {:url feed-url}
                       {:update-expr    "DELETE emails :email"
                        :expr-attr-vals {":email" #{email}}
                        :return         :all-new}))

(defn mark-url-as-updated
  [ddb feed-url post-url]
  (faraday/update-item ddb table {:url feed-url}
                       {:update-expr    "ADD post_urls_sent :urls"
                        :expr-attr-vals {":urls" #{post-url}}
                        :return         :all-new}))

(defn clear-marked-urls
  [ddb feed-url]
  (faraday/update-item ddb table {:url feed-url}
                       {:update-expr    "REMOVE post_urls_sent"
                        :return         :all-new}))

(defn emails-for-feed
  [ddb feed-url]
  (feed-attribute ddb feed-url :emails))

(defn post-urls-sent-for-feed
  [ddb feed-url]
  (feed-attribute ddb feed-url :post_urls_sent))

(defn all-urls
  [ddb]
  (map :url (faraday/scan ddb table)))