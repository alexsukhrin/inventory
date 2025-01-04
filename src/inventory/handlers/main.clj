(ns inventory.handlers.main
  (:require
   [hiccup2.core :as h]
   [inventory.handlers.page :as p]))

(def body
  [:body
   [:div {:class "min-h-screen bg-gray-100 flex justify-center items-center"}
    [:div {:class "bg-white p-8 rounded-lg shadow-md w-96"}

     [:h2 {:class "text-2xl font-bold mb-6 text-center"} "Login"]

     [:form {:action "/login" :method "post"}
      [:div {:class "mb-4"}
       [:label {:for "username" :class "block text-sm font-medium text-gray-700"} "Username"]
       [:input {:type "text" :id "username" :name "username" :required true
                :class "mt-1 p-2 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"}]]

      [:div {:class "mb-4"}
       [:label {:for "password" :class "block text-sm font-medium text-gray-700"} "Password"]
       [:input {:type "password" :id "password" :name "password" :required true
                :class "mt-1 p-2 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"}]]

      [:div {:class "flex items-center justify-between mb-6"}
       [:label {:class "flex items-center"}
        [:input {:type "checkbox" :name "remember" :class "mr-2"}]
        "Remember me"]
       [:a {:href "#" :class "text-sm text-indigo-600 hover:text-indigo-500"} "Forgot your password?"]]
      [:button {:type "submit"
                :class "w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-opacity-50"}
       "Login"]]]]])

(defn main-page [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (p/page body)})
