(ns scraper.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]
            [scraper.eywa-api :as api])
  (:gen-class))

;; incomplete url - append double digit number - 00, 01, ..., 23
(def url-tmpl "https://meteo.hr/podaci.php?section=podaci_vrijeme&param=hrvatska1_n&sat=")

(def re-time #"Vrijeme u Hrvatskoj (\d\d.\d\d.\d\d\d\d.) u (\d\d) h")

(defn get-urls [url]
  (map #(str url (format "%02d" %)) (range 24)))

(defn get-date-hour [dom]
  (let [header (html/text (first (html/select dom [:.glavni__content :h4])))
        [_ date hour] (re-matches re-time header)]
    [date hour]))

(defn convert-types [m] 
  (let [parse-float #(if (= % "-") nil (Float/parseFloat %))
        parse-int #(if (= % "-") nil (Integer/parseInt %))]
    (into
      {}
      (for [[k v] m] [k (cond
                          (contains? #{:wind_speed :temperature :pressure_tendency :pressure} k) (parse-float v)
                          (contains? #{:hour :humidity} k) (parse-int v)
                          :else v)]))))

(defn get-meas [dom date hour]
  (let [tds (html/select dom [[:#table-aktualni-podaci (html/attr-contains :class "fd-c-table1")] :td])
        build-map #(zipmap [:stname :wind_direction :wind_speed :temperature :humidity :pressure :pressure_tendency :weather] %)]
    (->>
      (map #(string/trim (html/text %)) tds)
      (partition-all 8)
      (map build-map)
      (map #(assoc % :date date :hour hour))
      (map #(merge % {:stname (string/replace (:stname %) #" A$" "")
                      :pressure (string/replace (:pressure %) #"\*$" "")}))
      (map convert-types)
      (map #(dissoc (merge % {:station {:name (:stname %)}}) :stname)))))  ; {:stname "..." ...} > {:station {:name "..."} ...}

(defn scrape [urls]
  (when-let [url (first urls)]
    (let [dom (html/html-resource (java.net.URL. url))
          [date hour] (get-date-hour dom)]
      (doseq [m (get-meas dom date hour)]
        (if-let [exists (api/measurement-exists m)]
          (println "Entry exists: " exists)
          (println (api/measurement-add m)))))
    (recur (rest urls))))

(defn -main [& _]
  (scrape (get-urls url-tmpl)))
