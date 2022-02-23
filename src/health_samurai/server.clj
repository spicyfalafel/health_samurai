(ns health-samurai.server
  (:require
   [immutant.web :as web]
   [compojure.route :as cjr]
   [compojure.core :as compojure]
   ;[clojure.data.json :as json]
   [cheshire.core :refer :all]
   [ring.middleware.cors :refer [wrap-cors]]
   [health-samurai.database :as db]))

;; GET /patient
;; POST /patient
;; PUT /patient/{id}
;; DELETE /patient/{id}


;; LocalDate json encoding
(extend-protocol cheshire.generate/JSONable
  java.time.LocalDate
  (to-json [dt gen]
    (cheshire.generate/write-string gen (str dt))))


(defn get-patients []
              (generate-string (db/get-patients) {:pretty true}))

(defn add-patient [request]
  (when-let [patient-map (:params request)]
    {
     :status 200
     :body (doall
            ;(db/ins-patient! patient-map)
            (str patient-map))
     })
  )


(compojure/defroutes routes
  (compojure/GET "/patient" [] {:body (get-patients)})
  (compojure/POST "/patient" request (add-patient request))
  (compojure/PUT "/patient/:id" [id] {:body (str "PUT patient with id " id) })
  (compojure/DELETE "/patient/:id" [id] {:body (str "DELETE patient with id " id )})
  (cjr/not-found "<h1>Page not found!!!</h1>"))


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
