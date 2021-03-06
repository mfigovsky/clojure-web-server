(ns clojure-web-server.web-server
  (:require
   [com.stuartsierra.component :as component]
   [catacumba.core :as ct]
   [catacumba.http :as http]
   [catacumba.handlers.parse :as parse]
   [clojure-web-server.db :refer [users] :as db]
   [cheshire.core :as json]))


(defn http-json-ok [data]
  (-> (json/encode data)
      (http/ok {:content-type "application/json"})))

(defn hello-world [req]
  (http/ok "hello"))

(defn get-users [{:keys [::db] :as req}]
  (http-json-ok (users db)))

(defn add-user! [{:keys [::db data]}]
  (db/add-user! db (:username data))
  (http/ok "user added"))

(defn remove-user! [{:keys [::db data]}]
  (db/remove-user! db (:username data))
  (http/ok "user removed"))

(defn api-routes []
  [[:any (parse/body-params)]
   [:get "hello" #'hello-world]
   [:get "users" #'get-users]
   [:post "user" #'add-user!]
   [:post "removeuser" #'remove-user!]])


(defrecord WebServer [port mongo-db]
  component/Lifecycle

  (start [component]
    (println ";; starting WebServer on port [%d]" port)
    (let [routes (concat [[:any (fn [_] (ct/delegate {::db mongo-db}))]]
                         (api-routes))]
      (assoc component :server (ct/run-server (ct/routes routes)
                                              {:port  port
                                               :host  "0.0.0.0"
                                               :debug true}))))

  (stop [component]
    (println ";; stopping WebServer")
    (when-let [server (:server component)]
      (.stop server))
    (dissoc component :server)))

(defn new-web-server [config]
  (component/using
   (map->WebServer config)
   [:mongo-db]))

(comment
  (def my-web-server (new-web-server {:port 3130}))
  (def vvv (component/start my-web-server))
  (component/stop vvv)



  (def server (component/start (new-web-server {:port 3129})))
  (component/stop server)

  )
