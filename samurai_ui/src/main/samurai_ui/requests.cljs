(ns samurai-ui.requests
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]] [cljs-http.client :as http]))

(defn my-get []
  (go (let [response (<! (http/get "http://localhost:8080/patient"
                                   {:with-credentials? false
                                    :Origin "http://localhost:8000"}))]
        (:body response))))


