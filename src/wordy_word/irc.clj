(ns wordy-word.irc
  (:require [wordy-word
             [generate :refer [generate]]
             [word-list :as words]]
            [irclj.core :as irc]
            [clojure
             [string :as string]
             [pprint :refer [pprint]]]))

(def nick "wordy-word")
(def channel "#wordy-word")

(def connection (atom nil))

(defn eat-log [& args]
  (comment pprint args))

(defn message! [msg]
  (irc/message @connection channel msg))

; TODO: vote namespace
(def ballot (atom nil))

(defn take-rand [n coll]
  (take n (shuffle coll)))

(defn make-ballot [kind size]
  (let [noun? (= :noun kind)
        word-list (if noun?
                    words/unapproved-nouns
                    words/unapproved-adjectives)]
    {:approved (if noun?
                 words/approved-nouns
                 words/approved-adjectives)
     :unapproved word-list
     :words (take-rand size @word-list)}))

(defn strip-yes-no [yes-no-str]
  (apply str (filter #(or (= % \y) (= % \n)) yes-no-str)))

(defn vote! [args]
  (let [start (re-matches #"(noun|adj) (\d+)" (first args))
        complete (re-matches #"[yn]+" (strip-yes-no (first args)))]
    (cond
      start (let [kind (keyword (second start))
                  size (Integer/parseInt (nth start 2))]
              (reset! ballot (make-ballot kind size))
              (message! (str "ballot: " (string/join ", " (:words @ballot)))))
      complete (let [keywords (map (comp keyword str) complete)]
                 (if-not (= (count keywords) (count (:words @ballot)))
                   (message! "invalid vote count")
                   (let [pairs (map vector (:words @ballot) keywords)
                         accepted (filter #(= :y (second %)) pairs)]
                     (words/accept! @ballot)
                     (reset! ballot nil)
                     (message! (format "accepted %d words" (count accepted))))))
      :else (message! "invalid vote command"))))

(defn gen! [args]
  (message! (string/join " " (generate))))

(def commands {:vote vote!
               :gen gen!})

(defn on-msg [irc msg]
  (when-let [command (re-matches #"\.(vote|gen) ?(.*)" (:text msg))]
    ((get commands (keyword (second command)))
     (drop 2 command))))

(defn disconnect! []
  (when @connection
    (swap! connection irc/kill)))

(defn connect! []
  (disconnect!)
  (reset! connection
          (irc/connect "irc.freenode.net"
                       6667 nick
                       :callbacks {:raw-log eat-log
                                   :privmsg on-msg}))
  (irc/join @connection channel))
