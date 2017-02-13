(defproject wordy-word "0.1.0-SNAPSHOT"
  :description "A silly name generator"
  :url "https://github.com/jeaye/wordy-word"
  :license {:name "jank license"
            :url "https://upload.jeaye.com/jank-license"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot wordy-word.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
