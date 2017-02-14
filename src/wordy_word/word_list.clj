(ns wordy-word.word-list
  (:require [clojure
             [string :as string]
             [pprint :refer [pprint]]]))

(defn read-lines [file]
  (try
    (->> (clojure.java.io/resource file)
         slurp
         clojure.string/split-lines
         (filter not-empty))
    (catch Throwable _
      [])))

(def unapproved-nouns (atom (read-lines "unapproved-nouns")))
(def unapproved-adjectives (atom (read-lines "unapproved-adjectives")))
(def approved-nouns (atom (read-lines "approved-nouns")))
(def approved-adjectives (atom (read-lines "approved-adjectives")))

(defn save! [words file]
  (->> (string/join "\n" @words)
       (spit (clojure.java.io/resource file))))

; TODO: Refactor out file names
(defn save-all! []
  (save! unapproved-nouns "unapproved-nouns")
  (save! unapproved-adjectives "unapproved-adjectives")
  (save! approved-nouns "approved-nouns")
  (save! approved-adjectives "approved-adjectives"))

(defn accept! [ballot]
  (swap! (:approved ballot) into (:words ballot))
  (swap! (:unapproved ballot) #(remove (into #{} (:words ballot)) %))
  (save-all!))
