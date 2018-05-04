(ns evently.app
  (:require [evently.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)

(core/mount-components)
