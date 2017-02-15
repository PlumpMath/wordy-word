(ns wordy-word.core
  (:gen-class)
  (:require [wordy-word
             [generate :refer [generate]]
             [irc :as irc]]))

(defn -main
  [& args]
  (irc/connect!)
  (loop []
    (Thread/sleep (* 1000 60 60))))
