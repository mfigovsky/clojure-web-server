(ns clojure-web-server.core
  (:gen-class)
  (:require
   [com.stuartsierra.component   :as component]
   [clojure-web-server.system   :refer [new-system]])
  (:use [compojure.route :only [files not-found]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server))

(def config
  {:web-server {:port 8087 }
   :mongo      {:uri "mongodb://127.0.0.1:27017/clojure-web-server"}})

(defroutes all-routes
           (GET "/newhello" [] "<p>New HELLO from http-kit.</p>")
           (not-found "<p>Page not found.</p>")) ;; all other, return 404

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (println "Hello, World!")
  (let [system (new-system config)]
    (component/start system)
    (run-server all-routes {:port 8080})))

(comment
  (keys com.stuartsierra.component.repl/system))
