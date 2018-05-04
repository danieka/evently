(ns evently.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary
             :include-macros true
             :refer [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(def secret-key (.from_base64 js/sodium "QdH9i7Uk0027HA0QnpMgEAxCIMkrltuYk4FFnuaER1k"))
(def secret-key-base64 (.to_base64 js/sodium secret-key))

(defn encrypt-string
  [s secret-key]
  (let [nonce (.randombytes_buf js/sodium (.-crypto_secretbox_NONCEBYTES js/sodium))]
    (str
      (.to_base64 js/sodium
        nonce)
      (.to_base64 js/sodium 
        (.crypto_secretbox_easy js/sodium s nonce secret-key)))))

(defn decrypt-string
  [s secret-key]
  (if (string? s)
    (do 
      (let [buf (.from_base64 js/sodium s)]
        (let [nonce (.slice buf 0 (.-crypto_secretbox_NONCEBYTES js/sodium))
              ciphertext (.slice buf (.-crypto_secretbox_NONCEBYTES js/sodium))]
          (.to_string js/sodium (.crypto_secretbox_open_easy js/sodium ciphertext nonce secret-key)))))
    s))

(defn encrypt
  [params secret-key]
  (into {} (map (fn [[k v]] [k (encrypt-string v secret-key)]) params)))

(defn decrypt
  [params secret-key]
  (into {} (map (fn [[k v]] [k (decrypt-string v secret-key)]) params)))

(defn post
  [url req]
  (POST url
    (into req {:format :json
               :headers
               {"Accept" "application/transit+json"
               "x-csrf-token" (.-value (.getElementById js/document "token"))}
               :params (into
                          (encrypt (:params req) secret-key)
                          {:acceskey secret-key-base64})})))

(defn get-event [event id]
  (GET (str "/event/" id)
    {:headers {"Accept"  "application/transit+json"}
     :handler #(reset! event (decrypt % secret-key))}))

(defn send-event! [fields errors]
  (post "/event"
    {:params @fields
     :handler #(.log js/console (str "response:" %))
     :error-handler #(do
                      (.error js/console (str %))
                      (reset! errors (get-in % [:response :errors])))}))

(defn errors-component
  [errors key]
  (when-let [error (key @errors)]
    [:div.alert.alert-danger (clojure.string/join error)]))

(defn event-form []
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [:div.content
        [:div.form-group
          [:p "Organizer:"
            [:input.form-control
              {:type :text
               :name :organizer
               :on-change #(swap! fields assoc :organizer (-> % .-target .-value))
               :value (:organizer @fields)}]]
          [errors-component errors :organizer]
          [:p "Description:"
            [:textarea.form-control
              {:rows 5
               :cols 50
               :name :description
               :on-change #(swap! fields assoc :description (-> % .-target .-value))}]]
          [errors-component errors :description]
          [:input.btn.btn-primary {:type :submit 
                                   :value "create event"
                                   :on-click #(send-event! fields errors)}]]])))

(defn display-event [id]
  (let [event (atom nil)]
    (get-event event id)
    (fn []
      [:div
        [:p (str "Organizer: " (:organizer @event))]
        [:p (str "Description: " (:description @event))]
        [:input.btn.btn-primary {:value "get event"
                                 :on-click #(get-event event id)}]])))                   

(defn home []
  [:div.row
    [:div.span12
      [:h2 "Create Event"]
      [event-form]]])

(defn page [page-component]
  (r/render-component
    [page-component]
    (.getElementById js/document "app")))

(defn mount-components
  []
  ())

(defroute home-path "/" [] (page home))
(defroute event-path "/event/:id" [id] (page (partial display-event id)))

(defn init! [] ())

(let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true)))