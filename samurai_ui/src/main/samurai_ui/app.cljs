(ns samurai-ui.app
  (:require [reagent.core :as r])
  (:require [reagent.dom :as dom])
  (:require [samurai-ui.requests :as myreq])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]] [cljs-http.client :as http])
  )


(defn init []
  (println "app initialized")
  (myreq/get-patients))


(defn Application []
  [:div.my-container
   [:h1.app-name "Patients"]
   [:button.add-patient-btn {:onClick (fn [] (myreq/get-patients))} "Add patient"]

   (doall
    (for [p @myreq/patients-atom]
      [:div.card
       [:h2 "Name: " (:firstname p) " "(:lastname p)]
       [:h2 "Gender: " (:gender p)]
       [:h2 "Birthdate: " (:birthdate p)]
       [:h2 "Address: " (:address p)]
       [:h2 "Polys: " (:polys_id p)]
       [:button {:onClick (fn [] (myreq/get-patients))} "Remove patient"]]
      ))
   ])

(dom/render [Application] (js/document.getElementById "app"))

