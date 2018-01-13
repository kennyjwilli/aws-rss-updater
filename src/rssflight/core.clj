(ns rssflight.core
  (:require
    [uswitch.lambada.core :as lambda]
    [taoensso.faraday :as faraday]
    [taoensso.timbre :as log]
    [rssflight.db :as db]
    [rssflight.feed :as feed]
    [rssflight.email :as email]))

(def ddb-opts db/ddb-opts)
(def email-client (email/mk-client))

(lambda/deflambdafn simplerss.AddSite
  [in out ctx]
  )

(defn posts-to-send
  "Returns a set of URIs that need to have post updates sent to the users."
  [feed-url]
  (let [urls-sent (db/post-urls-sent-for-feed ddb-opts feed-url)
        url-headers {}]
    (feed/posts-to-send feed-url url-headers urls-sent)))

(defn send-uri-update!
  [feed-url post-uri post-title]
  (let [emails (db/emails-for-feed ddb-opts feed-url)]
    (db/mark-url-as-updated ddb-opts feed-url post-uri)
    (when-not (empty? emails)
      (log/info "Sending post update..." :uri post-uri :n (count emails))
      (email/send-feed-update-email email-client emails feed-url post-title post-uri))
    nil))

;; 1. take in urls to get updates for (probably only one url for now)
;; 2. send email to users subscribed to that url
(defn do-feed-updates
  [feed-url]
  (let [posts (posts-to-send feed-url)]
    (when-not (empty? posts)
      (doseq [{:keys [uri title]} posts]
        (send-uri-update! feed-url uri title)))))

(lambda/deflambdafn rssflight.SendFeedUpdates
  [in out ctx]
  )

(defn initiate-update-impl
  []
  (let [urls (db/all-urls ddb-opts)]
    ))

(lambda/deflambdafn rssflight.InitiateUpdate
  [in out ctx]
  (initiate-update-impl))