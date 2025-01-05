(ns inventory.handlers.page
  (:require
   [hiccup2.core :as h]))

(defn head []
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:script {:src "https://cdn.tailwindcss.com"}]
   [:script {:src "https://unpkg.com/htmx.org"}]])

(defn actions [request]
  (let [role (get-role request)]
    [:div {:class "max-w-7xl mx-auto"}
     [:ul {:class "flex space-x-4"}
      (when (is-admin role)
        [:li
         [:button {:hx-get "/add-row"
                   :hx-target "#user-table-body"
                   :hx-swap "afterbegin"
                   :class "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"}
          "Add Record"]])
      [:li
       [:button {:class "bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                 :onclick "window.location.href='/download-csv'"}
        "Download CSV"]]
      [:li
       [:button {:class "bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600"
                 :onclick "window.print()"}
        "Print Report"]]]]))

(defn nav []
  [:nav
   [:ul {:class "flex space-x-4"}
    ;; [:li [:a {:href "/tables" :class "hover:text-gray-300"} "Tables"]]
    ;; [:li [:a {:href "/create-table" :class "hover:text-gray-300"} "Add table"]]
    ;; [:li [:a {:href "/edit-table" :class "hover:text-gray-300"} "Edit table"]]
    [:li [:a {:href "/logout" :class "hover:text-gray-300"} "Logout"]]]])

(defn header []
  [:header {:class "bg-blue-500 text-white p-4"}
   [:div {:class "max-w-7xl mx-auto flex justify-between items-center"}
    [:div
     [:h1 {:class "text-2xl font-bold"} "Inventory"]]
    (nav)]])

(defn page [body]
  (str (h/html [:html {:lang "en"} (head) body])))
