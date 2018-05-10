(ns evently.test.db.core
  (:require [evently.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [evently.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'evently.config/env
      #'evently.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

; (deftest test-events
;   (jdbc/with-db-transaction [t-conn *db*]
;     (jdbc/db-set-rollback-only! t-conn)
;     (is (= 1 (db/create-event!
;                t-conn
;                {:organizer    "Bobby McDermot"
;                 :description  "Party y'all! BYOB!"})))
;     (is (= [{:id         15
;             :organizer "Bobby McDermot"
;             :description  "Party y'all! BYOB!"}]
;            (db/get-all-events t-conn {:id "1"})))))
