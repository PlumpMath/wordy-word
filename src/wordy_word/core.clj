(ns wordy-word.core
  (:gen-class))

(defn read-lines [file]
  (try
    (-> (clojure.java.io/resource file)
        slurp
        clojure.string/split-lines)
    (catch Throwable _
      [])))

(def unapproved-nouns (atom (read-lines "unapproved-nouns")))
(def unapproved-adjectives (atom (read-lines "unapproved-adjectives")))
(def approved-nouns (atom (read-lines "approved-nouns")))
(def approved-adjectives (atom (read-lines "approved-adjectives")))

(defn rand-word [word-list generated]
  (if (empty? @word-list)
    generated
    (conj generated (rand-nth @word-list))))

(def rand-noun (partial rand-word approved-nouns))
(def rand-adjective (partial rand-word approved-adjectives))

; TODO: Alliterations
(def generators [[rand-noun]
                 [rand-noun rand-noun]
                 [rand-adjective rand-noun]
                 [rand-adjective rand-adjective rand-noun]])

(defn generate []
  (let [generator (rand-nth generators)
        generated (reduce #(%2 %1) [] generator)]
    generated))

(defn -main
  [& args]
  (let []))
