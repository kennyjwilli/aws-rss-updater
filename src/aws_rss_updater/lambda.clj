(ns aws-rss-updater.lambda
  (:require
    [clojure.java.io :as io]
    [cognitect.transit :as transit])
  (:import (com.amazonaws.services.lambda AWSLambdaClient)
           (com.amazonaws.services.lambda.model InvokeRequest CreateFunctionRequest FunctionCode UpdateFunctionCodeRequest)
           (java.nio ByteBuffer)
           (com.amazonaws.util IOUtils)
           (java.io ByteArrayOutputStream ByteArrayInputStream)))

;; - Create IAM policy
;; - Create IAM Role
;; - Create Lambda fn

(defn client
  [region]
  (.build
    (doto (AWSLambdaClient/builder)
      (.setRegion region))))

(defn encode-transit
  [obj]
  (with-open [out (ByteArrayOutputStream.)]
    (transit/write (transit/writer out :json) obj)
    (.toString out)))

(defn decode-transit
  [s]
  (with-open [in (ByteArrayInputStream. (.getBytes s))]
    (transit/read (transit/reader in :json))))

(defn invoke
  [client {:keys [function-name payload type]}]
  (let [req (doto (InvokeRequest.)
              (.setInvocationType ^String type)
              (.setFunctionName function-name)
              (.setPayload ^String (encode-transit payload)))]
    (.invoke client req)))

(defn invoke-async
  [client name-or-arn payload-obj]
  (invoke client {:function-name name-or-arn
                  :payload       payload-obj
                  :type          "Event"}))

(defn file->bytebuffer
  [file]
  (ByteBuffer/wrap
    (IOUtils/toByteArray (io/input-stream file))))

(defn create-function
  [client {:keys [file
                  function-name role-arn handler]}]
  (let [req (doto (CreateFunctionRequest.)
              (.withRuntime "java8")
              (.withFunctionName function-name)
              (.withCode (doto (FunctionCode.)
                           (.withZipFile (file->bytebuffer file))))
              (.withRole role-arn)
              (.withHandler handler)
              (.withMemorySize (int 1024))
              (.withTimeout (int 60)))]
    (.createFunction client req)))

(defn update-function-code
  [client {:keys [file function-name]}]
  (let [req (doto (UpdateFunctionCodeRequest.)
              (.withFunctionName function-name)
              (.withZipFile (file->bytebuffer file)))]
    (.updateFunctionCode client req)))