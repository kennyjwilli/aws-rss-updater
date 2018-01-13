(ns rssflight.email
  (:import (com.amazonaws.services.simpleemail AmazonSimpleEmailServiceClient)
           (com.amazonaws.regions Regions)
           (com.amazonaws.services.simpleemail.model Destination Body Content SendEmailRequest Message)))

(defn mk-client
  []
  (.withRegion (AmazonSimpleEmailServiceClient.)
               (Regions/US_EAST_1)))

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

;; look into bulk email sending
(defn send-feed-update-email
  [client emails feed-url post-title post-url]
  (doseq [email emails]
    (send-email client "rssflight@gmail.com" email
                (str "RSS Update: " feed-url)
                (str post-title ": " post-url))))