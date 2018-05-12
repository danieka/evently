(ns evently.routes.home
  (:require [evently.layout :as layout]
            [evently.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [evently.validation :as v]
            [ring.util.http-response :as response]
            [ring.util.response :refer [response status]]
            [buddy.hashers :as hashers]))

(defn home-page []
  (layout/render "home.html"))

(defn save-event! [{:keys [params]}]
  (if-let [errors (v/validate-event params)]
    (-> {:errors errors} response (status 400))
    (do
      (response {:status :ok :response (db/get-event {:id (last (last (
        db/create-event! (assoc
                            params
                            :access-key
                            (hashers/derive (:access-key params))))))})}))))

(defn get-event [id access-key]
  (println (str (:access-key (db/get-event-access-key {:id id})) "   "  access-key))
  (if (hashers/check access-key (:access-key (db/get-event-access-key {:id id})))
    (response/ok
      (let [d (db/get-event {:id id})]
        (println d)
        d))
    (response/not-found)))

(defn save-participation! [id access-key {:keys [params]}]
  (println (str (:access-key (db/get-event-access-key {:id id})) "   "  access-key))
  (if (hashers/check access-key (:access-key (db/get-event-access-key {:id id})))
    (do
      (db/create-participation! (assoc params :event id))
      (response/ok))
    (response/not-found)))

(defn get-participations-for-event [id access-key]
  (if (hashers/check access-key (:access-key (db/get-event-access-key {:id id})))
    (response/ok
      (let [d (db/get-participations-for-event {:event id})]
        (println d)
        d))
    (response/not-found)))

(defroutes home-routes
  (GET "/event/:id" [id access-key] (get-event id access-key))
  (POST "/event" req (save-event! req))
  (POST "/event/:id/participation" [id access-key :as req] (save-participation! id access-key req))
  (GET "/event/:id/participation" [id access-key] (get-participations-for-event id access-key))
  (GET "/" [] (home-page)))

