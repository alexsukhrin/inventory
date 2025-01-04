(ns inventory.server
  (:require
   [org.httpkit.server :as hk]
   [mount.core :as mount :refer [defstate]]
   [inventory.app :refer [app]]))

(defn start-server []
  (when-let [server (hk/run-server #'app {:port (Integer/parseInt (or (System/getenv "SERVER_PORT") "3000"))})]
    (println "Server has started!")
    server))

(defstate my-server
  :start (start-server)
  :stop  (my-server :timeout 100))