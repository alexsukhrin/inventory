(ns inventory.handlers.table
  (:require
   [inventory.handlers.page :as p]
   [hiccup2.core :as h]
   [cheshire.core :refer :all]
   [inventory.db :as db]
   [java-time :as jt]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]
   [ring.util.response :as response]
   [clojure.data.codec.base64 :as b64]))

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

(defn base64-valid? [s]
  (and (string? s)
       (re-matches #"[A-Za-z0-9+/=]+" s)))

(defn table [request]
  (let [role (get-role request)]
    [:div {:class "overflow-x-auto max-w-full"
           :id "user-table-body"}
     [:div {:class "max-h-screen overflow-y-auto border border-gray-300 rounded-lg"}
      [:table {:class "max-h-screen bg-white"}
       [:thead {:class "sticky top-0 bg-gray-200 shadow-md"}
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
       [:tbody
        (for [{:keys [id service accounting property_type name serial_number id_label actual_location status department
                      notes mvo nomenclature price accounting_name unit invoice last_changes photo_url photo_type]} (db/get-table)]
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
           [:td {:class "px-4 py-2 border-b text-gray-700"}
            (when (base64-valid? photo_url)
              (cond
                (= photo_type "image/png")
                [:a {:href (str "data:image/png;base64," photo_url) :download "image.png"}
                 [:img {:src (str "data:image/png;base64," photo_url) :alt "Фото" :class "max-w-xs"}]]

                (= photo_type "image/jpeg")
                [:a {:href (str "data:image/jpeg;base64," photo_url) :download "image.jpg"}
                 [:img {:src (str "data:image/jpeg;base64," photo_url) :alt "Фото" :class "max-w-xs"}]]

                (= photo_type "application/pdf")
                [:a {:href (str "data:application/pdf;base64," photo_url) :download "document.pdf"}
                 [:span {:class "text-blue-500"} "Download PDF"]]

                :else
                [:span "Unsupported file type"]))]
           (when (or (is-admin role) (is-edit role))
             [:td {:class "px-4 py-2 border-b"}
              [:button {:hx-post "/edit-row"
                        :hx-vals (generate-string {:row-id id})
                        :hx-target "#user-table-body"
                        :hx-swap "outerHTML"
                        :class "bg-yellow-500 text-white px-3 py-1 rounded flex items-center space-x-2"}
               [:svg {:xmlns "http://www.w3.org/2000/svg"
                      :fill "none"
                      :viewBox "0 0 24 24"
                      :stroke-width "1.5"
                      :stroke "currentColor"
                      :class "size-6"}
                [:path {:stroke-linecap "round"
                        :stroke-linejoin "round"
                        :d "m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"}]]]])
           (when (is-admin role)
             [:td {:class "px-4 py-2 border-b"}
              [:button {:hx-delete (str "/delete-row/" id)
                        :hx-target (str "#row-" id)
                        :hx-swap "outerHTML"
                        :class "bg-red-500 text-white px-3 py-1 rounded"}
               [:svg {:xmlns "http://www.w3.org/2000/svg"
                      :fill "none"
                      :viewBox "0 0 24 24"
                      :stroke-width "1.5"
                      :stroke "currentColor"
                      :class "size-6"}
                [:path {:stroke-linecap "round"
                        :stroke-linejoin "round"
                        :d "m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"}]]]])])]]]]))

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

(defn header [request]
  (let [role (get-role request)]
    [:header {:class "bg-blue-500 text-white p-4"}
     [:div {:class "max-w-7xl mx-auto flex justify-between items-center"}
      [:div
       [:h1 {:class "text-2xl font-bold"} "Inventory"]]
      [:nav
       [:ul {:class "flex space-x-4"}
        (when (is-admin role)
          [:li [:button {:hx-get "/add-row"
                         :hx-target "#user-table-body"
                         :hx-swap "outerHTML"
                         :class "hover:text-gray-300"} "Додати"]])
        [:li [:button {:onclick "window.location.href='/download-csv'"
                       :class "hover:text-gray-300"} "Завантажити"]]
        [:li [:button {:onclick "window.print()"
                       :class "hover:text-gray-300"} "Друк"]]
        [:li [:a {:href "/logout" :class "hover:text-gray-300"} "Вийти"]]]]]]))

(defn table-page [request]
  (response/response
   (p/page
    [:body {:class "bg-gray-100"}
     (header request)
     (table request)])))

(defn add-row [request]
  (response/response
   (str
    (h/html
     [:form {:method "post"
             :enctype "multipart/form-data"
             :action "/save-row"
             :class "w-full"}
      [:table {:class "table-auto w-full border-collapse border border-gray-200"}
       [:thead
        [:tr {:class "bg-gray-100"}
         [:th {:class "px-4 py-2 border"} "Field"]
         [:th {:class "px-4 py-2 border"} "Value"]]]
       [:tbody
        (for [[field name] [["Служба" "service"]
                            ["Облік" "accounting"]
                            ["Тип майна" "property_type"]
                            ["Найменування" "name"]
                            ["Серійний номер" "serial_number"]
                            ["ID|Наліпка" "id_label"]
                            ["Фактично знаходиться" "actual_location"]
                            ["Статус" "status"]
                            ["Підрозділ" "department"]
                            ["Примітки та допис" "notes"]
                            ["М.В.О." "mvo"]
                            ["Номенклатура" "nomenclature"]
                            ["Ціна" "price"]
                            ["Найменування по обліку" "accounting_name"]
                            ["Одиниця" "unit"]
                            ["Накладна" "invoice"]]]
          [:tr
           [:td {:class "px-4 py-2 border bg-gray-50"} field]
           [:td {:class "px-4 py-2 border"}
            [:input {:type "text"
                     :name name
                     :class "border rounded px-2 py-1 w-full"}]]])
        [:tr
         [:td {:class "px-4 py-2 border bg-gray-50"} "Фото"]
         [:td {:class "px-4 py-2 border"}
          [:input {:type "file"
                   :name "photo_url"
                   :accept ".png,.jpeg,.jpg,.pdf"
                   :class "border rounded px-2 py-1 w-full"}]]]]]
      [:div {:class "mt-4 flex justify-center gap-2"}
       [:button {:type "submit"
                 :class "bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"}
        "Зберегти"]
       [:button {:type "submit"
                 :href "/table"
                 :class "bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"}
        "Відмінити"]]]))))

(defn to-byte-array [input-stream]
  (with-open [baos (java.io.ByteArrayOutputStream.)]
    (io/copy input-stream baos)
    (.toByteArray baos)))

(defn encode-to-base64 [file-path]
  (try
    (let [file-bytes (io/input-stream file-path)]
      (String. (b64/encode (to-byte-array file-bytes))))
    (catch Exception e (str "caught exception: " (.getMessage e)))))

(defn save-row [request]
  (let [{:keys [service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice photo_url]} (:params request)
        {:keys [content-type tempfile]} photo_url
        base64-data (encode-to-base64 tempfile)]
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
                    :photo_url base64-data
                    :photo_type content-type})
    (response/redirect "/table")))

(defn delete-add-row-btn [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body nil})

(defn delete-row [row-id]
  (db/delete-row {:row-id (Integer/parseInt row-id)})
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body nil})

(defn delete-photo [id]
  (db/update-photo {:id (Integer/parseInt id)
                    :photo_url nil
                    :photo_type nil
                    :last_changes (jt/local-date-time)})
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body nil})

(defn edit-row [request]
  (let [role (get-role request)
        {:keys [id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice last_changes photo_url photo_type]}
        (db/get-record {:row-id (-> request :params :row-id Integer/parseInt)})]
    (response/response
     (str
      (h/html
       [:form {:method "post"
               :enctype "multipart/form-data"
               :action "/save-edit-row"
               :class "w-full"}
        [:table {:class "table-auto w-full border-collapse border border-gray-200"}
         [:thead
          [:tr {:class "bg-gray-100"}
           [:th {:class "px-4 py-2 border"} "Field"]
           [:th {:class "px-4 py-2 border"} "Value"]]]
         [:tbody
          [:tr
           [:td {:class "px-4 py-2 border"}
            [:input {:type "hidden"
                     :name "id"
                     :value id
                     :class "border rounded px-2 py-1 w-full"}]]]
          (for [[field name value] [["Служба" "service" service]
                                    ["Облік" "accounting" accounting]
                                    ["Тип майна" "property_type" property_type]
                                    ["Найменування" "name" name]
                                    ["Серійний номер" "serial_number" serial_number]
                                    ["ID|Наліпка" "id_label" id_label]
                                    ["Фактично знаходиться" "actual_location" actual_location]
                                    ["Статус" "status" status]
                                    ["Підрозділ" "department" department]
                                    ["Примітки та допис" "notes" notes]
                                    ["М.В.О." "mvo" mvo]
                                    ["Номенклатура" "nomenclature" nomenclature]
                                    ["Ціна" "price" price]
                                    ["Найменування по обліку" "accounting_name" accounting_name]
                                    ["Одиниця" "unit" unit]
                                    ["Накладна" "invoice" invoice]]]
            [:tr
             [:td {:class "px-4 py-2 border bg-gray-50"} field]
             [:td {:class "px-4 py-2 border"}
              [:input {:type "text"
                       :name name
                       :value value
                       :class "border rounded px-2 py-1 w-full"}]]])
          [:tr
           [:td {:class "px-4 py-2 border bg-gray-50"} "Фото"]
           [:td {:class "px-4 py-2 border"}
            [:div {:id "photo-container"}
             [:input {:type "hidden" :name "existing_photo_url" :value photo_url}]
             [:input {:type "hidden" :name "existing_photo_type" :value photo_type}]
             (when (base64-valid? photo_url)
               [:div
                [:p "Поточне зображення:"]
                (cond
                  (= photo_type "image/png")
                  [:a {:href (str "data:image/png;base64," photo_url) :download "image.png"}
                   [:img {:src (str "data:image/png;base64," photo_url) :alt "Зображення" :class "max-w-xs"}]]

                  (= photo_type "image/jpeg")
                  [:a {:href (str "data:image/jpeg;base64," photo_url) :download "image.jpg"}
                   [:img {:src (str "data:image/jpeg;base64," photo_url) :alt "Зображення" :class "max-w-xs"}]]

                  (= photo_type "application/pdf")
                  [:a {:href (str "data:application/pdf;base64," photo_url) :download "document.pdf"}
                   [:span {:class "text-blue-500"} "Завантажити PDF"]]

                  :else
                  [:span "Unsupported file type"])

                [:button {:type "button"
                          :hx-delete (str "/delete-photo/" id)
                          :hx-target "#photo-container"
                          :hx-swap "outerHTML"
                          :class "bg-red-500 text-white px-3 py-1 mt-4 rounded hover:bg-red-600"}
                 "Видалити зображення"]])]

            [:input {:type "file"
                     :name "photo_url"
                     :accept ".png,.jpeg,.jpg,.pdf"
                     :value photo_url
                     :class "border rounded px-2 py-1 w-full"}]]]]]
        [:div {:class "mt-4 flex justify-center gap-2"}
         [:button {:type "submit"
                   :class "bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"} "Зберегти"]
         [:button {:type "button"
                   :onclick "window.location.href='/table';"
                   :class "bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"} "Відмінити"]]])))))

(defn valid-content-type? [content-type]
  (some #(= content-type %) ["application/pdf" "image/png" "image/jpeg"]))

(defn save-edit-row [request]
  (let [role (get-role request)
        {:keys [id service accounting property_type name serial_number id_label actual_location status department
                notes mvo nomenclature price accounting_name unit invoice photo_url existing_photo_url existing_photo_type]} (:params request)
        {:keys [content-type tempfile]} photo_url
        new-file (encode-to-base64 tempfile)
        photo (if (base64-valid? new-file) new-file existing_photo_url)]
    (db/update-record {:row-id (Integer/parseInt id)
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
                       :photo_url photo
                       :photo_type (if (valid-content-type? content-type) content-type existing_photo_type)})
    (response/redirect "/table")))

(defn generate-csv [data]
  (let [headers (map name (keys (first data)))] ; Заголовки колонок
    (with-open [writer (io/writer "report.csv")]
      (csv/write-csv writer (cons headers (map vals data))))))

(defn download-csv-handler []
  (let [data (db/get-report)]
    (generate-csv data)
    (-> (response/file-response "report.csv")
        (assoc :headers {"Content-Disposition" "attachment; filename=\"data.csv\""}))))
