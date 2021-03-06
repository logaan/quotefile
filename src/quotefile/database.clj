(ns quotefile.database
  (:require [clojure.contrib.sql :as sql])
  (:import (java.sql DriverManager)))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite" ; Protocol to use
         :subname "db/db.sqlite3" ; Location of db
         :create true})

(defn create-database
  "Creates the table for this model"
  []
  (sql/with-connection
   db
   (sql/transaction
    (sql/create-table
     :quotes
     [:id :integer "PRIMARY KEY AUTOINCREMENT"]
     [:quote "text"]))))

(defn insert-quote
  "Inserts a quote into the database"
  [quote]
  (sql/with-connection db
    (sql/insert-records "quotes"
      {:quote quote})))

(defn select-quotes
  "Grabs all quotes from the database"
  []
  (sql/with-connection db
    (sql/with-query-results results ["select * from quotes"]
      (doall results))))

(defn select-quote
  "Grabs one quote from the database"
  [id]
  (sql/with-connection db
    (sql/with-query-results results [(str "select * from quotes where id=" id)]
      (nth (doall results) 0))))

(defn delete-quote
  "Delete a quote"
  [id]
  (sql/with-connection db
    (sql/transaction
      (sql/delete-rows :quotes ["id=?" id]))))

