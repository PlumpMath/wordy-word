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

; TODO: Refactor to rand-word
(defn rand-noun [generated]
  (if (empty? @approved-nouns)
    generated
    (conj generated (rand-nth @approved-nouns))))
(defn rand-adjective [generated]
  (if (empty? @approved-adjectives)
    generated
    (conj generated (rand-nth @approved-adjectives))))

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
