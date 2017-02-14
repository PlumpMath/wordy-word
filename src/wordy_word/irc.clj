(ns wordy-word.irc
  (:require [wordy-word
             [generate :refer [generate]]]
            [irclj.core :as irc]
            [clojure.pprint :refer [pprint]]))

(def nick "wordy-word")
(def channel "#wordy-word")

(def connection (atom nil))

(defn eat-log [& args]
  (comment pprint args))

(defn message! [msg]
  (irc/message @connection channel msg))

(defn vote! [args]
  (message! "ready to vote"))

(defn stop! [args]
  (message! "stopping"))

(defn gen! [args]
  (message! (clojure.string/join " " (generate))))

(def commands {:vote vote!
               :stop stop!
               :gen gen!})

(defn on-msg [irc msg]
  (when-let [command (re-matches #"\.(vote|stop|gen) ?(.*)" (:text msg))]
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
