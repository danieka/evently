(ns evently.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[evently started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[evently has shut down successfully]=-"))
   :middleware identity})
