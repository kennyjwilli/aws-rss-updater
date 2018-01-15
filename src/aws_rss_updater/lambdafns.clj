(ns aws-rss-updater.lambdafns
  (:require
    [clojure.spec.alpha :as s]
    [uswitch.lambada.core :as lambda]
    [cognitect.transit :as transit]
    [taoensso.timbre :as log]
    [aws-rss-updater.api :as api]
    [aws-rss-updater.model :as model])
  (:gen-class))

;; TODO: Load env from Lambda environment
(defn load-settings
  []
  {:bucket                "kenny-rss-stuff"
   :region                "us-east-1"
   :send-feed-updates-arn ""})

(s/def ::feed-updates-input
  (s/keys :req-un [::model/feed ::model/email]))

(lambda/deflambdafn rss.SendFeedUpdates
  [in out ctx]
  (let [data (transit/read (transit/reader in :json))]
    (if (s/valid? ::feed-updates-input data)
      (let [feed (:feed data)
            email (:email data)]
        (log/info "Starting feed update..." :url (:feed/url feed) :email email)
        (api/do-feed-updates (load-settings) feed email))
      (log/error "Invalid input data!"
                 :data data
                 :explain (s/explain-str ::feed-updates-input data)))))

;; get a list of all the feeds user is subscribed to and invoke an lambda fn for
;; each one. pass feed options to the lambda function
(lambda/deflambdafn rss.InitiateUpdate
  [in out ctx]
  (log/info "Initiating all feed update...")
  (api/initiate-update (load-settings)))

(lambda/deflambdafn rss.TestFunction
  [in out ctx]
  (println (pr-str ctx))
  (println (type ctx))
  (let [x (transit/read (transit/reader in :json))]
    (println (pr-str x))
    (println (:a x))))