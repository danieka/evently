(ns evently.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [clj-time.jdbc]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [evently.config :refer [env]]))

(defstate ^:dynamic *db*
          :start (conman/connect! {:jdbc-url (env :database-url)})
          :stop (conman/disconnect! *db*))

(defn clob-to-string [clob]
  "Turn an Oracle Clob into a String"
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Clob
  (result-set-read-column [value metadata index]
    (clob-to-string value)))
          

(conman/bind-connection *db* "sql/queries.sql")

