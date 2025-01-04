(ns inventory.core
  (:gen-class)
  (:require
   [mount.core :as mount]
   [inventory.db :as db]
   [inventory.server :as server]))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

;; Main entry point
(defn -main []
  (start))
