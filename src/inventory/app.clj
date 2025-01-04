(ns inventory.app
  (:require
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.session.memory :refer [memory-store]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [compojure.core :refer [defroutes routes GET POST PUT DELETE context]]
   [compojure.route :refer [not-found]]
   [inventory.handlers.table :as table-handler]
   [inventory.handlers.main :as main-handler]
   [inventory.handlers.login :as login-handler]
   [inventory.handlers.page :as p]))

(defn wrap-authorization [handler]
  (fn [request]
    (if (contains? #{:full :edit :read} (get-in request [:session :user-role]))
      (handler request)
      {:status 403
       :headers {"Content-Type" "text/html"}
       :body (p/page login-handler/login-failed-body)})))

(defroutes public-routes
  (GET      "/ping"               _        (table-handler/ping))
  (GET      "/"                   request  (main-handler/main-page request))
  (POST     "/login"              request  (login-handler/login-page request)))

(defroutes protected-routes
  (-> (routes
       (POST     "/save-row"           request  (table-handler/save-row request))
       (POST     "/save-edit-row"      request  (table-handler/save-edit-row request))
       (POST     "/delete-add-row-btn" request  (table-handler/delete-add-row-btn request))
       (POST     "/edit-row"           request  (table-handler/edit-row request))
       (DELETE   "/delete-row/:row-id" [row-id] (table-handler/delete-row row-id))
       (GET      "/logout"             request  (login-handler/logout-handler request))
       (GET      "/table"              request  (table-handler/table-page request))
       (GET      "/download-csv"       []       (table-handler/download-csv-handler))
       (GET      "/add-row"            request  (table-handler/add-row request)))
      wrap-authorization))

(defroutes app-routes
  public-routes
  protected-routes
  (not-found "<h1>404 Error!</h1>"))

(def app
  (-> #'app-routes
      (wrap-keyword-params)
      (wrap-params)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-session {:store (memory-store)})))


