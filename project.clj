(defproject scraper "0.1.0"
  :description "DHMZ daily weather report scraper"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [enlive "1.1.6"]
                 [clj-http "3.13.0"]
                 [org.clojure/data.json "2.5.0"]]
  :repl-options {:init-ns scraper.core}
  :main scraper.core
  :plugins [[cider/cider-nrepl "0.48.0"]]
  :profiles {:uberjar {:aot [scraper.core]}}
)
