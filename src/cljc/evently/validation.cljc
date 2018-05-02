(ns evently.validation
  (:require [struct.core :as st]))

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