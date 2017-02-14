(ns wordy-word.generate
  (:require [clojure.pprint :refer [pprint]]))

(defn read-lines [file]
  (try
    (-> (clojure.java.io/resource file)
        slurp
        clojure.string/split-lines)
    (catch Throwable _
      [])))

(def unapproved-nouns (atom (read-lines "unapproved-nouns")))
(def unapproved-adjectives (atom (read-lines "unapproved-adjectives")))
(def approved-nouns (atom (read-lines "unapproved-nouns")))
(def approved-adjectives (atom (read-lines "unapproved-adjectives")))

(defn rand-word [word-list generated]
  (if (empty? @word-list)
    generated
    (conj generated (rand-nth @word-list))))

(defn rand-alliterative-word [word-list generated]
  (cond
    (empty? @word-list) generated
    (empty? generated) (rand-word word-list generated) ; No prior word to follow
    :else
    (let [word (rand-nth generated)
          letter (first word)
          possible (filter #(= letter (first %)) @word-list)]
      (if (empty? possible)
        generated
        (conj generated (rand-nth possible))))))

(defn cute-words [word-list]
  (let [cute? #(or (= \y (last %))
                   (re-matches #".+ie" %))]
    ; Just to allow the @ later
    (delay (filter cute? @word-list))))

(def rand-noun (partial rand-word approved-nouns))
(def rand-adjective (partial rand-word approved-adjectives))
(def rand-alliterative-noun (partial rand-alliterative-word approved-nouns))
(def rand-alliterative-adjective (partial rand-alliterative-word approved-adjectives))
(def rand-cute-noun (partial rand-word (cute-words approved-nouns)))
(def rand-cute-adjective (partial rand-word (cute-words approved-adjectives)))
(def rand-cute-alliterative-noun (partial rand-alliterative-word (cute-words approved-nouns)))
(def rand-cute-alliterative-adjective (partial rand-alliterative-word (cute-words approved-adjectives)))

(def generators [[rand-noun]
                 [rand-noun rand-noun]
                 [rand-noun rand-alliterative-noun]
                 [rand-adjective rand-noun]
                 [rand-adjective rand-alliterative-noun]
                 [rand-cute-adjective rand-noun]
                 [rand-cute-adjective rand-cute-noun]
                 [rand-cute-adjective rand-alliterative-noun]
                 [rand-cute-adjective rand-cute-alliterative-noun]
                 [rand-adjective rand-adjective rand-noun]
                 [rand-adjective rand-alliterative-adjective rand-noun]
                 [rand-adjective rand-adjective rand-alliterative-noun]
                 [rand-adjective rand-alliterative-adjective rand-alliterative-noun]
                 ])

(defn generate []
  (let [generator (rand-nth generators)
        generated (reduce #(%2 %1) [] generator)]
    generated))
