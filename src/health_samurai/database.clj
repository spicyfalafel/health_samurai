(ns health-samurai.database
  (:require
   [honey.sql :as sql]
   [clojure.java.jdbc :as jdbc]
   ;;[clojure.string :as string]
   ;;[honeysql.format :as sqlf]
   [honey.sql.helpers :refer :all :as h]
   [java-time :as time])
   (:import [java.sql Timestamp]
            [java.sql Date]
            [java.time.format DateTimeFormatter]
            [java.time LocalDate]
            [java.time Instant]
            [java.io FileWriter]))


(def pg-db {:dbtype "postgresql"
            :dbname "postgres"
            :host "localhost"
            :user "postgres"
            :password "root"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; date insertion fix
(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Timestamp
  (result-set-read-column [v _ _]
    (.toInstant v))
  java.sql.Date
  (result-set-read-column [v _ _]
    (.toLocalDate v)))

(extend-protocol jdbc/ISQLValue
  java.time.Instant
  (sql-value [v]
    (Timestamp/from v))
  java.time.LocalDate
  (sql-value [v]
    (Date/valueOf v)))


(defmethod print-method java.time.Instant
  [inst out]
  (.write out (str "#time/inst \"" (.toString inst) "\"") ))

(defmethod print-dup java.time.Instant
  [inst out]
  (.write out (str "#time/inst \"" (.toString inst) "\"") ))

(defmethod print-method LocalDate
  [^LocalDate date ^FileWriter out]
  (.write out (str "#time/ld \"" (.toString date) "\"")))

(defmethod print-dup LocalDate
  [^LocalDate date ^FileWriter out]
  (.write out (str "#time/ld \"" (.toString date) "\"")))

(defn parse-date [string]
  (LocalDate/parse string))

(defn parse-time [string]
  (and string (-> (.parse (DateTimeFormatter/ISO_INSTANT) string)
                  Instant/from)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn get-patients []
  (jdbc/query pg-db (sql/format {:select [:*]
                                 :from [:patient]})))

(defn del-patient! [id]
  (jdbc/delete! pg-db :patient ["id = ?" id]))


(defn replace-birthdate-str [patient]
  (if-let [birthdate-str (:birthdate patient)]
    (assoc patient :birthdate (parse-date birthdate-str))
    patient))

(defn upd-patient! [patient]
  ;; если в параметре есть birthdate, значит надо его поменять на объект даты
  ;; если нет, значит менять не надо
  (jdbc/update! pg-db :patient (replace-birthdate-str patient)
                ["id = ?" (:id patient)]))

(defn ins-patient! [patient]
  (let [pat (assoc patient :birthdate  (parse-date (:birthdate patient)))]
   (jdbc/insert! pg-db :patient pat)))

(comment
  (def pat {:firstname "a"
            :lastname "b"
            :gender_id 1
            :birthdate #time/ld "2000-01-01"
            :address "ffsfff"
            :polys_id 4444414444444444})


  (ins-patient! pat)

  (upd-patient! 13 {:lastname "C"}) ;; ok

  (upd-patient! (assoc pat :id 13)) ;; ok

  (upd-patient! 100 {:lastname "C"})

  (get-patients)

  (del-patient 13)

  )
