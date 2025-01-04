(ns inventory.handlers.table
  (:require
   [inventory.handlers.page :as p]
   [hiccup2.core :as h]
   [cheshire.core :refer :all]
   [inventory.db :as db]
   [java-time :as jt]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]
   [ring.util.response :as response]))

(defn ping []
  {:body {:message "pong"}})

(defn get-role [request]
  (get-in request [:session :user-role]))

(defn is-admin [role]
  (= :full role))

(defn is-edit [role]
  (= :edit role))

(defn is-reader [role]
  (= :read role))

(defn table [request]
  (let [role (get-role request)]
    [:table {:class "table-auto w-full border-collapse border border-gray-300"}
     [:thead
      [:tr
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Служба"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Облік"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Тип майна"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Найменування"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Серійний номер"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "ID|Наліпка"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Фактично знаходиться"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Статус"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Підрозділ"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Примітки та дописи"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "М.В.О."]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Номенклатура"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Ціна"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Найменування по обліку"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Одиниця"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Накладна"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Останні зміни"]
       [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Фото"]
       (when (or (is-admin role) (is-edit role))
         [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Редагувати"])
       (when (is-admin role)
         [:th {:class "px-4 py-2 border-b bg-gray-100 font-medium text-left"} "Видалити"])]]
     [:tbody {:id "user-table-body"}
      (for [{:keys [id service accounting property_type name serial_number id_label actual_location status department
                    notes mvo nomenclature price accounting_name unit invoice last_changes photo_url]} (db/get-table)]
        [:tr {:id (str "row-" id)}
         [:td {:class "px-4 py-2 border-b text-gray-700"} service]
         [:td {:class "px-4 py-2 border-b text-gray-700"} accounting]
         [:td {:class "px-4 py-2 border-b text-gray-700"} property_type]
         [:td {:class "px-4 py-2 border-b text-gray-700"} name]
         [:td {:class "px-4 py-2 border-b text-gray-700"} serial_number]
         [:td {:class "px-4 py-2 border-b text-gray-700"} id_label]
         [:td {:class "px-4 py-2 border-b text-gray-700"} actual_location]
         [:td {:class "px-4 py-2 border-b text-gray-700"} status]
         [:td {:class "px-4 py-2 border-b text-gray-700"} department]
         [:td {:class "px-4 py-2 border-b text-gray-700"} notes]
         [:td {:class "px-4 py-2 border-b text-gray-700"} mvo]
         [:td {:class "px-4 py-2 border-b text-gray-700"} nomenclature]
         [:td {:class "px-4 py-2 border-b text-gray-700"} price]
         [:td {:class "px-4 py-2 border-b text-gray-700"} accounting_name]
         [:td {:class "px-4 py-2 border-b text-gray-700"} unit]
         [:td {:class "px-4 py-2 border-b text-gray-700"} invoice]
         [:td {:class "px-4 py-2 border-b text-gray-700"} last_changes]
         [:td {:class "px-4 py-2 border-b text-gray-700"} photo_url]
         (when (or (is-admin role) (is-edit role))
           [:td {:class "px-4 py-2 border-b"}
            [:button {:hx-post "/edit-row"
                      :hx-vals (generate-string {:row-id id})
                      :hx-target (str "#row-" id)
                      :hx-swap "outerHTML"
                      :class "bg-yellow-500 text-white px-3 py-1 rounded"} "Edit"]])
         (when (is-admin role)
           [:td {:class "px-4 py-2 border-b"}
            [:button {:hx-delete (str "/delete-row/" id)
                      :hx-target (str "#row-" id)
                      :hx-swap "outerHTML"
                      :class "bg-red-500 text-white px-3 py-1 rounded"} "Delete"]])])]]))

(defn paginations []
  [:div {:class "flex justify-center mt-4"}
   [:nav {:aria-label "Pagination"}
    [:ul {:class "inline-flex items-center space-x-2"}
     [:li
      [:button {:class "px-3 py-1 border border-gray-300 rounded-l-lg bg-gray-100 hover:bg-gray-200 text-gray-700"}
       "Попередня"]]
     (for [page (range 1 6)]
       [:li {:key page}
        [:button {:class (str "px-3 py-1 border border-gray-300 bg-white hover:bg-gray-200 text-gray-700 "
                              (if (= page 1) "font-bold text-blue-500" ""))}
         page]])
     [:li
      [:button {:class "px-3 py-1 border border-gray-300 rounded-r-lg bg-gray-100 hover:bg-gray-200 text-gray-700"}
       "Наступна"]]]]])

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

(defn table-page [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (p/page
          [:body (p/header)
           [:main {:class "min-h-screen bg-gray-50 p-6"}
            (actions request)
            [:div {:class "overflow-x-auto mt-4"}
             (table request)
             (paginations)]]])})

(defn add-row [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (h/html
               [:tr {:id "add-row"}
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "service" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "accounting" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "property_type" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "name" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "serial_number" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "id_label" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "actual_location" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "status" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "department" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "notes" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "mvo" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "nomenclature" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "price" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "accounting_name" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "unit" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "text" :name "invoice" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "hidden" :name "last_changes" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"} [:input {:type "file" :name "photo_url" :class "border rounded px-2 py-1"}]]
                [:td {:class "px-4 py-2 border-b"}
                 [:button {:hx-post "/save-row"
                           :hx-include "closest tr"
                           :hx-target "#add-row"
                           :hx-swap "outerHTML"
                           :class "bg-green-500 text-white px-3 py-1 rounded"} "Save"]]
                [:td {:class "px-4 py-2 border-b"}
                 [:button {:hx-post "/delete-add-row-btn"
                           :hx-swap "outerHTML"
                           :hx-target "#add-row"
                           :class "bg-red-500 text-white px-3 py-1 rounded"} "Delete"]]]))})

(defn save-row [request]
  (let [{:keys [service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice photo_url]} (:params request)
        {:keys [id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice last_changes photo_url]}
        (db/add-record {:service service
                        :accounting accounting
                        :property_type property_type
                        :name name
                        :serial_number serial_number
                        :id_label id_label
                        :actual_location actual_location
                        :status status
                        :department department
                        :notes notes
                        :mvo mvo
                        :nomenclature nomenclature
                        :price price
                        :accounting_name accounting_name
                        :unit unit
                        :invoice invoice
                        :last_changes (jt/local-date-time)
                        :photo_url photo_url})]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str
            (h/html
             [:tr {:id (str "row-" id)}
              [:td {:class "px-4 py-2 border-b text-gray-700"} service]
              [:td {:class "px-4 py-2 border-b text-gray-700"} accounting]
              [:td {:class "px-4 py-2 border-b text-gray-700"} property_type]
              [:td {:class "px-4 py-2 border-b text-gray-700"} name]
              [:td {:class "px-4 py-2 border-b text-gray-700"} serial_number]
              [:td {:class "px-4 py-2 border-b text-gray-700"} id_label]
              [:td {:class "px-4 py-2 border-b text-gray-700"} actual_location]
              [:td {:class "px-4 py-2 border-b text-gray-700"} status]
              [:td {:class "px-4 py-2 border-b text-gray-700"} department]
              [:td {:class "px-4 py-2 border-b text-gray-700"} notes]
              [:td {:class "px-4 py-2 border-b text-gray-700"} mvo]
              [:td {:class "px-4 py-2 border-b text-gray-700"} nomenclature]
              [:td {:class "px-4 py-2 border-b text-gray-700"} price]
              [:td {:class "px-4 py-2 border-b text-gray-700"} accounting_name]
              [:td {:class "px-4 py-2 border-b text-gray-700"} unit]
              [:td {:class "px-4 py-2 border-b text-gray-700"} invoice]
              [:td {:class "px-4 py-2 border-b text-gray-700"} last_changes]
              [:td {:class "px-4 py-2 border-b text-gray-700"} photo_url]
              [:td {:class "px-4 py-2 border-b"}
               [:button {:hx-post "/edit-row"
                         :hx-vals (generate-string {:row-id id})
                         :hx-target (str "#row-" id)
                         :hx-swap "outerHTML"
                         :class "bg-yellow-500 text-white px-3 py-1 rounded"} "Edit"]]
              [:td {:class "px-4 py-2 border-b"}
               [:button {:hx-delete (str "/delete-row/" id)
                         :hx-target (str "#row-" id)
                         :hx-swap "outerHTML"
                         :class "bg-red-500 text-white px-3 py-1 rounded"} "Delete"]]]))}))

(defn delete-add-row-btn [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body nil})

(defn delete-row [row-id]
  (db/delete-row {:row-id (Integer/parseInt row-id)})
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body nil})

(defn edit-row [request]
  (let [role (get-role request)
        {:keys [id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice last_changes photo_url]}
        (db/get-record {:row-id (-> request :params :row-id Integer/parseInt)})]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str
            (h/html
             [:tr {:id (str "row-" id)}
              [:td {:class "px-4 py-2 border-b"} [:input {:value service :type "text" :name "service" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value accounting :type "text" :name "accounting" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value property_type :type "text" :name "property_type" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value name :type "text" :name "name" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value serial_number :type "text" :name "serial_number" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value id_label :type "text" :name "id_label" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value actual_location :type "text" :name "actual_location" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value status :type "text" :name "status" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value department :type "text" :name "department" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value notes :type "text" :name "notes" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value mvo :type "text" :name "mvo" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value nomenclature :type "text" :name "nomenclature" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value price :type "text" :name "price" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value accounting_name :type "text" :name "accounting_name" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value unit :type "text" :name "unit" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value invoice :type "text" :name "invoice" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value last_changes :type "hidden" :name "last_changes" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"} [:input {:value photo_url :type "file" :name "photo_url" :class "border rounded px-2 py-1"}]]
              [:td {:class "px-4 py-2 border-b"}
               [:button {:hx-post "/save-edit-row"
                         :hx-include "closest tr"
                         :hx-swap "outerHTML"
                         :hx-target (str "#row-" id)
                         :hx-vals (generate-string {:row-id id})
                         :class "bg-green-500 text-white px-3 py-1 rounded"} "Save"]]
              (when (is-admin role)
                [:td {:class "px-4 py-2 border-b"}
                 [:button {:hx-delete (str "/delete-row/" id)
                           :hx-swap "outerHTML"
                           :hx-target (str "#row-" id)
                           :class "bg-red-500 text-white px-3 py-1 rounded"} "Delete"]])]))}))

(defn save-edit-row [request]
  (let [role (get-role request)
        {:keys [row-id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice photo_url]} (:params request)
        {:keys [id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice last_changes photo_url]}
        (db/update-record {:row-id (Integer/parseInt row-id)
                           :service service
                           :accounting accounting
                           :property_type property_type
                           :name name
                           :serial_number serial_number
                           :id_label id_label
                           :actual_location actual_location
                           :status status
                           :department department
                           :notes notes
                           :mvo mvo
                           :nomenclature nomenclature
                           :price price
                           :accounting_name accounting_name
                           :unit unit
                           :invoice invoice
                           :last_changes (jt/local-date-time)
                           :photo_url photo_url})]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str
            (h/html
             [:tr {:id (str "row-" id)}
              [:td {:class "px-4 py-2 border-b text-gray-700"} service]
              [:td {:class "px-4 py-2 border-b text-gray-700"} accounting]
              [:td {:class "px-4 py-2 border-b text-gray-700"} property_type]
              [:td {:class "px-4 py-2 border-b text-gray-700"} name]
              [:td {:class "px-4 py-2 border-b text-gray-700"} serial_number]
              [:td {:class "px-4 py-2 border-b text-gray-700"} id_label]
              [:td {:class "px-4 py-2 border-b text-gray-700"} actual_location]
              [:td {:class "px-4 py-2 border-b text-gray-700"} status]
              [:td {:class "px-4 py-2 border-b text-gray-700"} department]
              [:td {:class "px-4 py-2 border-b text-gray-700"} notes]
              [:td {:class "px-4 py-2 border-b text-gray-700"} mvo]
              [:td {:class "px-4 py-2 border-b text-gray-700"} nomenclature]
              [:td {:class "px-4 py-2 border-b text-gray-700"} price]
              [:td {:class "px-4 py-2 border-b text-gray-700"} accounting_name]
              [:td {:class "px-4 py-2 border-b text-gray-700"} unit]
              [:td {:class "px-4 py-2 border-b text-gray-700"} invoice]
              [:td {:class "px-4 py-2 border-b text-gray-700"} last_changes]
              [:td {:class "px-4 py-2 border-b text-gray-700"} photo_url]
              [:td {:class "px-4 py-2 border-b"}
               [:button {:hx-post "/edit-row"
                         :hx-vals (generate-string {:row-id id})
                         :hx-target (str "#row-" id)
                         :hx-swap "outerHTML"
                         :class "bg-yellow-500 text-white px-3 py-1 rounded"} "Edit"]]
              (when (is-admin role)
                [:td {:class "px-4 py-2 border-b"}
                 [:button {:hx-delete (str "/delete-row/" id)
                           :hx-target (str "#row-" id)
                           :hx-swap "outerHTML"
                           :class "bg-red-500 text-white px-3 py-1 rounded"} "Delete"]])]))}))

(defn generate-csv [data]
  (let [headers (map name (keys (first data)))] ; Заголовки колонок
    (with-open [writer (io/writer "report.csv")]
      (csv/write-csv writer (cons headers (map vals data))))))

(defn download-csv-handler []
  (let [data (db/get-table)]
    (generate-csv data)
    (-> (response/file-response "report.csv")
        (assoc :headers {"Content-Disposition" "attachment; filename=\"data.csv\""}))))
