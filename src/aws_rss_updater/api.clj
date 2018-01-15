(ns aws-rss-updater.api
  (:require
    [taoensso.timbre :as log]
    [aws-rss-updater.feed :as feed]
    [aws-rss-updater.email :as email]
    [aws-rss-updater.model :as model]
    [aws-rss-updater.lambda :as lambda]))

(def test-feed "http://prog21.dadgum.com/atom.xml")

(defn posts-to-send
  "Returns a set of URIs that need to have post updates sent to the user."
  [{:feed/keys [url sent]}]
  (let [url-headers {}]
    (feed/posts-to-send url url-headers sent)))

(defn send-uri-update!
  [settings feed email post-uri post-title]
  (let [feed-url (:feed/url feed)
        email-client (email/mk-client (:region settings))]
    (model/mark-post-as-sent! settings feed-url post-uri)
    (log/info "Sending post update..." :uri post-uri :email email)
    (email/send-feed-update-email email-client email feed-url post-title post-uri)
    nil))

(defn do-feed-updates
  [settings feed email]
  (let [posts (posts-to-send feed)]
    (if (empty? posts)
      (log/info "No new posts." :feed (:feed/url feed))
      (do
        (log/info "Feed updates found. Sending post updates..."
                  :feed (:feed/url feed)
                  :post-uris (mapv :uri posts))
        (doseq [{:keys [uri title]} posts]
          (send-uri-update! settings feed email uri title))))))

(defn invoke-feed-updater
  "Asynchronously invokes the `aws-rss-updater.SendFeedUpdates` lambda fn, passing
  `feed` and `email` to the function."
  [settings feed email]
  (lambda/invoke-async (lambda/client (:region settings))
                       (:send-feed-updates-arn settings)
                       {:feed  feed
                        :email email}))

(defn initiate-update
  [settings]
  (let [state (model/get-state settings)
        email (::model/email state)]
    (doseq [feed (::model/feeds state)]
      (log/info "Start update function for" (:feed/url feed))
      (invoke-feed-updater settings feed email))))