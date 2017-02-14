(ns wordy-word.irc
  (:require [wordy-word
             [generate :refer [generate]]
             [word-list :as words]]
            [irclj.core :as irc]
            [clojure.string :refer [join]]
            [clojure.pprint :refer [pprint]]))

(def nick "wordy-word")
(def channel "#wordy-word")

(def connection (atom nil))

(defn eat-log [& args]
  (comment pprint args))

(defn message! [msg]
  (irc/message @connection channel msg))

; TODO: vote namespace
(def ballot (atom []))

(defn take-rand [n coll]
  (take n (shuffle coll)))

(defn make-ballot [kind size]
  (take-rand size (if (= :noun kind)
                    @words/unapproved-nouns
                    @words/unapproved-adjectives)))

(defn strip-yes-no [yes-no-str]
  (apply str (filter #(or (= % \y) (= % \n)) yes-no-str)))

(defn vote! [args]
  (pprint args)
  (let [start (re-matches #"(noun|verb) (\d+)" (first args))
        complete (re-matches #"(y|n)+" (strip-yes-no (first args)))]
    (cond
      start (let [kind (keyword (second start))
                  size (Integer/parseInt (nth start 2))]
              (reset! ballot (make-ballot kind size))
              (message! (str "ballot: " (join ", " @ballot))))
      complete (let []
                 )
      :else (message! "invalid vote command"))))

(defn gen! [args]
  (message! (join " " (generate))))

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
