(ns scraper.eywa-api
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn login-details []
  {:host (System/getenv "EYWA_HOST")
   :username (System/getenv "EYWA_USERNAME")
   :password (System/getenv "EYWA_PASSWORD")})

(defn get-token []
  (let [{host :host username :username password :password} (login-details)]
    (:body (client/post (str host "/eywa/login")
                        {:content-type :json
                         :body (format "{\"username\":\"%s\",\"password\":\"%s\"}" username password)}))))

(def token (get-token))

;; json body holds graphql payload
(def json-add-tmpl (slurp (io/resource "add.json.tmpl")))
(def json-check-tmpl (slurp (io/resource "check.json.tmpl")))

(defn graphql-post [json-body]
  (client/post (str (:host (login-details)) "/graphql") 
                      {:content-type :json
                       :headers {"Authorization" (str "Bearer " token)} 
                       :body json-body}))

(defn measurement-add [m]
  (:body (graphql-post (format json-add-tmpl (json/write-str m)))))

(defn measurement-exists [m]
  (let [body (:body (graphql-post (format json-check-tmpl
                               (json/write-str (:date m)) 
                               (:hour m) 
                               (json/write-str (-> m :station :name)))))]
    (-> (json/read-str body :key-fn keyword) :data :searchMeasurement)))
