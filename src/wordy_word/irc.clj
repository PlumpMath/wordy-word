(ns wordy-word.irc
  (:require [irclj.core :as irc]))

(def nick "wordy-word")
(def channel "#wordy-word")

(def connection (atom nil))

(defn eat-log [& args]
  (comment pprint args))

(defn disconnect! []
  (when @connection
    (swap! connection irc/kill)))

(defn connect! []
  (disconnect!)
  (reset! connection (irc/connect "irc.freenode.net"
                                  6667 nick
                                  :callbacks {:raw-log eat-log}))
  (irc/join @connection channel))

(defn message! [msg]
  (irc/message @connection channel msg))
