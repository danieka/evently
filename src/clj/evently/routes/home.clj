(ns evently.routes.home
  (:require [evently.layout :as layout]
            [evently.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [struct.core :as st]))

(defn home-page [{:keys [flash]}]
  (layout/render
    "home.html"
    (merge {:events (db/get-all-events)}
      (select-keys flash [:organizer :description :errors]))))

(defn about-page []
  (layout/render "about.html"))

  (def event-schema
    [[:organizer
      st/required
      st/string]
  
     [:description
      st/required
      st/string
      {:message "description must contain at least 10 characters"
       :validate #(> (count %) 9)}]])
  
(defn validate-event [params]
  (first (st/validate params event-schema)))

(defn save-event! [{:keys [params]}]
  (if-let [errors (validate-event params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-event! params)
      (response/found "/"))))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/event" request (save-event! request))
  (GET "/about" [] (about-page)))

