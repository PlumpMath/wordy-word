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
  (let [cute? (#{:cute-noun :cute-adj} kind)
        modifier (if cute?
                   cute-words
                   identity)
        m (cond
            (or (= :noun kind)
                (= :cute-noun kind)) {:approved words/approved-nouns
                                      :unapproved words/unapproved-nouns}
            (or (= :adj kind)
                (= :cute-adj kind)) {:approved words/approved-adjectives
                                     :unapproved words/unapproved-adjectives})]
    (merge m {:words (take-rand size @(modifier (:unapproved m)))})))

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
