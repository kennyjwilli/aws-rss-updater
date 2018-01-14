(ns dev.cloudwatch
  (:import (com.amazonaws.services.cloudwatchevents AmazonCloudWatchEventsClient)
           (com.amazonaws.services.cloudwatchevents.model PutRuleRequest PutTargetsRequest Target)))

(defn client
  [^String region]
  (.build
    (doto (AmazonCloudWatchEventsClient/builder)
      (.withRegion region))))

(defn mk-rule
  "Makes a CloudWatch rule named `rule-name` that is invoked every `every-mins`,
  calling `target-arn`."
  [client rule-name target-arn every-mins]
  (let [rule-req (doto (PutRuleRequest.)
                   (.setName rule-name)
                   (.setScheduleExpression (format "rate(%s minutes)" every-mins)))
        target-req (doto (PutTargetsRequest.)
                     (.setRule rule-name)
                     (.setTargets [(doto (Target.)
                                     (.setId "lambdafn")
                                     (.setArn target-arn))]))]
    (.putRule client rule-req)
    (.putTargets client target-req)))

(defn init
  [region rule-name lambda-fn-name]
  (mk-rule (client region) rule-name lambda-fn-name 60))