(ns wordy-word.irc
  (:require [wordy-word
             [generate :refer [generate cute-words]]
             [vote :as vote]
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

(defn gen! [args]
  (message! (string/join " " (generate))))

(defn vote! [args]
  (message! (vote/vote! args)))

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
