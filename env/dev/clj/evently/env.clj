(ns evently.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [evently.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[evently started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[evently has shut down successfully]=-"))
   :middleware wrap-dev})
