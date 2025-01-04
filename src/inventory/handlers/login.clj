(ns inventory.handlers.login
  (:require
   [inventory.handlers.page :as p]
   [ring.util.response :as response]))

(def users
  {:admin {:password (System/getenv "ADMIN") :permission :full}
   :user1 {:password (System/getenv "USER1") :permission :edit}
   :user2 {:password (System/getenv "USER2") :permission :read}})

(def login-failed-body
  [:body
   [:div {:class "min-h-screen bg-gray-100 flex justify-center items-center"}
    [:div {:class "bg-white p-8 rounded-lg shadow-md w-96 text-center"}
     [:h2 {:class "text-xl font-bold mb-4 text-red-600"} "Login Failed"]
     [:p {:class "text-gray-700 mb-6"} "The username or password you entered is incorrect."]
     [:a {:href "/"
          :class "w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded-md inline-block"}
      "Back to Login"]]]])

(defn login-page [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        user (get users (keyword username))]
    (if (= password (:password user))
      (-> (response/redirect "/table")
          (assoc :session (assoc (:session request) :user-role (:permission user))))
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (p/page login-failed-body)})))

(defn logout-handler [request]
  (-> (response/redirect "/")
      (assoc :session nil)))
