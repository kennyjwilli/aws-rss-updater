(ns aws-rss-updater.lambdafns
  (:require
    [uswitch.lambada.core :as lambda]
    [aws-rss-updater.api :as api])
  (:gen-class))

;; TODO: Load env from Lambda environment
(defn load-settings
  []
  {:bucket                "kenny-rss-stuff"
   :region                "us-east-1"
   :send-feed-updates-arn ""})

(lambda/deflambdafn rss.SendFeedUpdates
  [in out ctx]
  ;; TODO: parse lambda in
  (let [feed ""
        email ""]
    (api/do-feed-updates (load-settings) feed email)))

;; get a list of all the feeds user is subscribed to and invoke an lambda fn for
;; each one. pass feed options to the lambda function
(lambda/deflambdafn rss.InitiateUpdate
  [in out ctx]
  (api/initiate-update (load-settings)))

(lambda/deflambdafn rss.TestFunction
  [in out ctx]
  (println (pr-str in))
  (println (type in)))