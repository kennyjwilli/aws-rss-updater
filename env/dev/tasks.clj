(ns dev.tasks
  {:boot/export-tasks true}
  (:require
    [boot.core :as boot]
    [boot.task.built-in :as tasks]))

(boot/deftask uberjar
  [n jar-name VAL str "Name of the JAR file."]
  (assert jar-name ":jar-name is required.")
  (comp
    (tasks/aot :namespace #{'aws-rss-updater.lambdafns})
    (tasks/uber)
    (tasks/jar :file jar-name)
    (tasks/sift :include #{(re-pattern jar-name)})))

(boot/deftask init
  "Initialize the environment."
  [b bucket VAL str "S3 bucket to initialize with."
   r region VAL str "S3 bucket region."]
  (assert bucket ":bucket is required.")
  ;; TODO: Init lambda functions
  ;; Build uberjar for lambda functions
  ;; must upload rss.SendFeedUpdates before rss.InitiateUpdate so we can set
  ;; the arn of rss.SendFeedUpdates in rss.InitiateUpdate environment
  ;; Upload to AWS
  ;; Get ARNs
  (require 'dev.init)
  (let [region (or region "us-east-1")
        lambda-arn ""]
    ((resolve 'dev.init/init-all) region bucket lambda-arn))
  identity)