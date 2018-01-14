(ns aws-rss-updater.lambda
  (:require
    [taoensso.nippy :as nippy])
  (:import (com.amazonaws.services.lambda AWSLambdaClient)
           (com.amazonaws.services.lambda.model InvokeRequest)
           (java.nio ByteBuffer)))

;; - Create IAM policy
;; - Create IAM Role
;; - Create Lambda fn

(defn client
  [region]
  (.build
    (doto (AWSLambdaClient/builder)
      (.setRegion region))))

(defn invoke-async
  [client name-or-arn payload-obj]
  (let [payload (ByteBuffer/wrap (nippy/freeze payload-obj))
        req (doto (InvokeRequest.)
              (.setInvocationType "Event")
              (.setFunctionName name-or-arn)
              (.setPayload payload))]
    (.invoke client req)))