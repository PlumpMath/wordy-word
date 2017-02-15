(ns wordy-word.word-list
  (:require [clojure
             [string :as string]
             [pprint :refer [pprint]]]))

(defn read-lines [file]
  (try
    (->> (slurp file)
         clojure.string/split-lines
         (filter not-empty))
    (catch Throwable _
      [])))

(def unapproved-nouns-file "unapproved-nouns")
(def unapproved-adjectives-file "unapproved-nouns")
(def approved-nouns-file "approved-nouns")
(def approved-adjectives-file "approved-adjectives")

(def unapproved-nouns (atom (read-lines unapproved-nouns-file)))
(def unapproved-adjectives (atom (read-lines unapproved-adjectives-file)))
(def approved-nouns (atom (read-lines approved-nouns-file)))
(def approved-adjectives (atom (read-lines approved-adjectives-file)))

(defn save! [words file]
  (->> (string/join "\n" @words)
       (spit (clojure.java.io/resource file))))

(defn save-all! []
  (save! unapproved-nouns unapproved-nouns-file)
  (save! unapproved-adjectives unapproved-adjectives-file)
  (save! approved-nouns approved-nouns-file)
  (save! approved-adjectives approved-adjectives-file))

(defn accept! [ballot]
  (swap! (:approved ballot) into (:words ballot))
  (swap! (:unapproved ballot) #(remove (into #{} (:words ballot)) %))
  (save-all!))
