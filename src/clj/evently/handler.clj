(ns evently.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [evently.layout :refer [error-page]]
            [evently.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [evently.env :refer [defaults]]
            [mount.core :as mount]
            [evently.middleware :as middleware]
            [ring.middleware.gzip]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats)
          (wrap-routes ring.middleware.gzip/wrap-gzip))
      (route/not-found
        (:body
          (error-page {:status 404
                       :title "page not found"}))))))
