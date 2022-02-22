(ns samurai-ui.app
  (:require [reagent.core :as r])
  (:require [reagent.dom :as dom])
  (:require [samurai-ui.requests :as myreq])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]] [cljs-http.client :as http])
  )

(def t (r/atom nil))

(defn get-request []
  (go (<! (http/get "http://localhost:8080/patient"
                    {:with-credentials? false
                     :Origin "http://localhost:8000"} ;; CORS
                    ))))

(defn read-response [channel]
  (go (let [resp (<! channel)]
        (reset! t (:body resp)))))

;(print (reset! t {:a "a"}))

(read-response (get-request))


(defn init []
  (println "app initialized"))


(defn Application []
  [:div
   [:h1 "App works"]
   @t
   ])

(dom/render [Application] (js/document.getElementById "app"))

