(ns health-samurai.server
  (:require
   [immutant.web :as web]
   [compojure.route :as cjr]
   [compojure.core :as compojure]
   [clojure.data.json :as json]
   [ring.middleware.cors :refer [wrap-cors]]
   ))

;; GET /patient
;; POST /patient
;; PUT /patient/{id}
;; DELETE /patient/{id}

(def patients
  [
   {
    :id 1
    :first-name "Anton"
    :surname "Andreev"
    :birth-date "2000-01-01"
    :sex "MALE"
    :address "Pushkin street, 1/10"
    :med-policy-id "22832213371488"
    }
   {
    :id 2
    :first-name "Andrey"
    :surname "Bogdanov"
    :birth-date "2000-02-02"
    :sex "MALE"
    :address "Pushkin street, 2/10"
    :med-policy-id "32832213371488"
    }
   ])
(compojure/defroutes routes
  (compojure/GET "/patient" [] {:body (str patients)})
  (compojure/POST "/patient" [] {:body "patients post"})
  (compojure/PUT "/patient/:id" [id] {:body (str "PUT patient with id " id) })
  (compojure/DELETE "/patient/:id" [id] {:body (str "DELETE patient with id " id )}))


(def app (-> routes
             (wrap-cors :access-control-allow-origin [#"http://localhost:8000"] ;; CORS
                        :access-control-allow-methods [:get :post]
                        :access-control-allow-headers ["Origin" "X-Requested-With" "Content-Type" "Accept"]
             )))

(defn -main [& args]
  (let [args-map (apply array-map args)
        port-str (or (get args-map "-p")
                     (get args-map "--port")
                     "8080")]
    (println "Starting web server on port" port-str)
    (web/run #'app { :port (Integer/parseInt port-str) })))


(comment
  (def server (-main "--port" "8080"))
  (web/stop server))
