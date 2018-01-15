(ns aws-rss-updater.email
  (:import (com.amazonaws.services.simpleemail AmazonSimpleEmailServiceClient)
           (com.amazonaws.services.simpleemail.model Destination Body Content SendEmailRequest Message)))

(defn mk-client
  [^String region]
  (.build
    (doto (AmazonSimpleEmailServiceClient/builder)
      (.withRegion region))))

(defn ->vec
  [x]
  (if (vector? x) x [x]))

(defn send-email-req
  [from to subject body]
  (let [destination (.withToAddresses (Destination.) (->vec to))
        body (.withText (Body.) (Content. body))
        message (Message. (Content. subject) body)]
    (SendEmailRequest. from destination message)))

(defn send-email
  [client from to subject body]
  (.sendEmail
    client
    (send-email-req from to subject body)))

(defn send-feed-update-email
  [client email feed-url post-title post-url]
  (send-email client email email
              (str "RSS Update: " feed-url)
              (str post-title ": " post-url)))