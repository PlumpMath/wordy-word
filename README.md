# wordy-word

This is a Valentine's Day gift to my wife, who wanted something to generate
names for her to call me. It reads from Princeton's Wordnet dictionary and
builds names from various generators which explicitly target certain forms which
she found appealing.

Since the dictionary used has nearly 100K nouns and adjectives, there's an
in-built IRC bot which enables word voting. During this process, the bot
presents words to a channel and users can choose whether or not to discard them
from the dictionary. The resultant dictionary is fine-tuned per the user's
taste.

## Some of her favorites
```clojure
["husbandly" "basketball"]
["steely" "wedgie"]
```

## Some of my favorites
```clojure
["lean" "pneumonic" "placenta"]
["tangelo" "tulipwood"]
["unblessed" "odorous" "oleoresin"]
```
