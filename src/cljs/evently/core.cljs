(ns evently.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary
             :include-macros true
             :refer [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(declare nav!)

; This master key is used when derivating access key from the secret key
(def master-key (.from_base64 js/sodium "WOSKESds5RVJaJYV3_rA1CDkVunkDz9tdfCQPYUWO7M"))

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

(defn access-key
  [secret-key]
  (.to_base64 js/sodium (.crypto_kdf_derive_from_key js/sodium (.-crypto_secretbox_KEYBYTES js/sodium) 195972984 secret-key master-key)))

(defn post
  [url req secret-key]
  (POST url
    (into req {:format :json
               :headers
               {"Accept" "application/transit+json"
               "x-csrf-token" (.-value (.getElementById js/document "token"))}
               :params (into
                          (encrypt (:params req) secret-key)
                          {:access-key (access-key secret-key)})})))

(defn get-event [event id secret-key]
  (.then (.-ready js/sodium))
    (GET (str "/event/" id "?access-key=" (access-key secret-key))
      {:headers {"Accept"  "application/transit+json"}
      :handler #(reset! event (decrypt % secret-key))}))

(defn send-event! [fields errors]
  (let [secret-key (.crypto_secretbox_keygen js/sodium)]
    (post "/event"
      {:params @fields
       :handler #(nav! (str "/event/" (get-in % [:response :id]) "?secret-key=" (.to_base64 js/sodium secret-key)))
       :error-handler #(do
                        (.error js/console (str %))
                        (reset! errors (get-in % [:response :errors])))} secret-key)))


(defn errors-component
  [errors key]
  (when-let [error (key @errors)]
    [:div.error (clojure.string/join error)]))

(defn event-form []
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [:form.event
        [:h1 "Create Event"]
        [:p
          [:label "Title"]
          [:input
            {:type :text
              :name :title
              :on-change #(swap! fields assoc :title (-> % .-target .-value))
              :value (:title @fields)}]
          [errors-component errors :title]]
        [:p
          [:label "Who"]
          [:input
            {:type :text
              :name :organizer
              :on-change #(swap! fields assoc :organizer (-> % .-target .-value))
              :value (:organizer @fields)}]
          [errors-component errors :organizer]]
        [:p
          [:label "When"]
          [:input
            {:type :date
              :name :start-date
              :on-change #(swap! fields assoc :start-date (-> % .-target .-value))}]
          [:input
            {:type :time
              :name :start-time
              :on-change #(swap! fields assoc :start-time (-> % .-target .-value))}]
          [errors-component errors :start-date]
          [errors-component errors :start-time]]
        [:p
          [:label "Where"]
          [:input
            {:type :text
              :name :location
              :on-change #(swap! fields assoc :location (-> % .-target .-value))
              :value (:location @fields)}]
          [errors-component errors :location]]
        [:p
          [:label "What"]
          [:textarea
            {:name :description
            :on-change #(swap! fields assoc :description (-> % .-target .-value))}]
          [errors-component errors :description]]
       [:button.primary {:on-click #(do (send-event! fields errors) false)} "create event" ]])))

(defn display-event [id queryparams]
  (let [event (atom nil)]
    (.log js/console (str queryparams))
    (get-event event id (.from_base64 js/sodium (:secret-key queryparams)))
    (fn []
      [:div.event
        [:h1 (:title @event)]
        [:p "You can share this link with friends to invite them."
          [:a {:href (.-location js/window)} (str (.-location js/window)) ]]
        [:p 
          [:label "Who"]
          [:span (:organizer @event)]]
        [:p
          [:label "When"]
          [:span (:start-date @event) " " (:start-time @event)]]
        [:p
          [:label "Where"]
          [:span (:location @event)]]
        [:p 
          [:label "What"]
          [:span (:description @event)]]])))                   

(defn home []
  [event-form])

(defn page [page-component]
  (r/render-component
    [page-component]
    (.getElementById js/document "app")))

(defn mount-components
  []
  ())

(defroute home-path "/" [] (page home))
(defroute event-path "/event/:id" [id query-params] (page (partial display-event id query-params)))

(defn init! [] ())

(defonce history 
  (let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true))))

(defn nav! [token]
  (.setToken history token))