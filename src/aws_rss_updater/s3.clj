(ns aws-rss-updater.s3
  (:require
    [taoensso.nippy :as nippy])
  (:import (com.amazonaws.services.s3 AmazonS3Client)
           (java.io ByteArrayInputStream)
           (com.amazonaws.services.s3.model ObjectMetadata S3Object)
           (com.amazonaws.util IOUtils)))

(defn client
  [^String region]
  (.build
    (doto (AmazonS3Client/builder)
     (.withRegion region))))

(defn create-bucket
  [client bucket-name]
  (.createBucket client bucket-name))

(defn put-object
  [client bucket key obj]
  (let [bytes (nippy/freeze obj)
        meta (doto (ObjectMetadata.)
               (.setContentLength (alength bytes)))]
    (with-open [is (ByteArrayInputStream. bytes)]
      (.putObject client bucket key is meta))))

(defn get-object
  [client bucket key]
  (let [obj ^S3Object (.getObject client bucket key)]
    (with-open [is (.getObjectContent obj)]
      (nippy/thaw (IOUtils/toByteArray is)))))

(defn update-object
  [client bucket key f]
  (let [new-obj (f (get-object client bucket key))]
    (put-object client bucket key new-obj)
    new-obj))

(defn create-state-file
  [])