(def project 'basic-rss)
(def version "0.1-alpha1")

(set-env! :resource-paths #{"src"}
          :source-paths #{"test" "env"}
          :dependencies '[[adzerk/boot-test "1.2.0" :scope "test"]
                          [boot/core "2.7.2" :scope "test"]
                          [provisdom/boot-lambda "0.1.2-alpha2" :scope "test"]
                          [com.amazonaws/aws-java-sdk-events "1.11.256" :scope "test"]

                          [org.clojure/clojure "1.9.0"]
                          [uswitch/lambada "0.1.2"]
                          [http-kit "2.2.0"]
                          [com.taoensso/timbre "4.10.0"]
                          [com.amazonaws/aws-java-sdk-ses "1.11.256"]
                          [com.amazonaws/aws-java-sdk-s3 "1.11.256"]
                          [com.amazonaws/aws-java-sdk-lambda "1.11.256"]
                          [org.clojars.kennyjwilli/feedparser-clj "0.6.0"]
                          [com.taoensso/nippy "2.14.0"]
                          [com.cognitect/transit-clj "0.8.300"]])

(require
  '[adzerk.boot-test :refer [test]]
  '[dev.tasks :refer [init uberjar]]
  '[provisdom.boot-lambda :refer [create-function]])

(task-options!
  pom {:project     project
       :version     version
       :description "FIXME: write description"
       :url         "http://example/FIXME"
       :scm         {:url "https://github.com/yourname/basic-rss"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}}
  jar {:main 'basic-rss.core
       :file (str "basic-rss-" version "-standalone.jar")})
