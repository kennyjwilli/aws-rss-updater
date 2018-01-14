(ns dev.init
  (:require
    [dev.cloudwatch :as cloudwatch]
    [aws-rss-updater.model :as model]))

(def cloudwatch-rule-name "update-rss-feeds")

(defn init-all
  "Initialize all AWS resources."
  [region bucket lambda-arn]
  (model/init {:bucket bucket :region region})
  (cloudwatch/init region cloudwatch-rule-name lambda-arn))