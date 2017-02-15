(ns wordy-word.vote
  (:require [wordy-word
             [generate :refer [generate cute-words]]
             [word-list :as words]]
            [clojure
             [string :as string]
             [pprint :refer [pprint]]]))

(def ballot (atom nil))

(defn take-rand [n coll]
  (take n (shuffle coll)))

(defn make-ballot [kind size]
  (let [m (cond
            (= :noun kind) {:approved words/approved-nouns
                            :unapproved words/unapproved-nouns}
            (= :cute-noun kind) {:approved (cute-words words/approved-nouns)
                                 :unapproved (cute-words words/unapproved-nouns)}
            (= :adj kind) {:approved words/approved-adjectives
                           :unapproved words/unapproved-adjectives}
            (= :cute-adj kind) {:approved (cute-words words/approved-adjectives)
                                :unapproved (cute-words words/unapproved-adjectives)})]
    {:words (take-rand size @(:unapproved m))}))

(defn strip-yes-no [yes-no-str]
  (apply str (filter #(or (= % \y) (= % \n)) yes-no-str)))

(defn vote! [args]
  (let [start (re-matches #"(noun|cute-noun|adj|cute-adj) (\d+)" (first args))
        complete (re-matches #"[yn]+" (strip-yes-no (first args)))]
    (cond
      start (let [kind (keyword (second start))
                  size (Integer/parseInt (nth start 2))]
              (reset! ballot (make-ballot kind size))
              (str "ballot: " (string/join ", " (:words @ballot))))
      complete (let [keywords (map (comp keyword str) complete)]
                 (if-not (= (count keywords) (count (:words @ballot)))
                   "invalid vote count"
                   (let [pairs (map vector (:words @ballot) keywords)
                         accepted (filter #(= :y (second %)) pairs)]
                     (words/accept! (assoc @ballot
                                           :words (map first accepted)))
                     (reset! ballot nil)
                     (format "accepted %d words" (count accepted)))))
      :else "invalid vote command")))
