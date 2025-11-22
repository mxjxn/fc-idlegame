(ns fc-idlegame.canned-replies)

;; ============================================================================
;; CANNED REPLIES LIBRARY
;; Level 1-5: Basic, simple replies
;; Level 5-10: Slightly more sophisticated replies
;; ============================================================================

(def replies-level-1-5
  [;; Friendly/Supportive replies
   "this is great"
   "love this"
   "interesting take"
   "agreed"
   "makes sense"
   "good point"
   "thanks for sharing"
   "helpful"
   "same here"
   "totally"
   "facts"
   "100%"
   "based"
   "real"
   "few"
   "ngmi"
   "gm"
   "lfg"
   "ser"
   "wagmi"
   
   ;; Casual/Conversational
   "yeah i think so too"
   "hadn't thought about it that way"
   "this resonates"
   "feeling this"
   "big if true"
   "this is the way"
   "exactly"
   "preach"
   "couldn't agree more"
   "you get it"
   "this hits different"
   "needed to hear this"
   "saving this"
   "bookmarking"
   "this aged well"
   "calling it now"
   "remind me in 1 year"
   "this is why i'm here"
   "quality content"
   "keep it coming"
   
   ;; Questions/Engagement
   "can you explain more?"
   "tell me more"
   "how so?"
   "what do you think about..."
   "interesting, hadn't considered that"
   "why do you think that is?"
   "curious to hear your thoughts"
   "what's your take on..."
   "would love to hear more"
   "this makes me think..."
   
   ;; Crypto/Web3 specific (but accessible)
   "bullish on this"
   "this is the future"
   "web3 is wild"
   "onchain is the way"
   "decentralized everything"
   "own your data"
   "this is why crypto matters"
   "building in public"
   "gm gm"
   "wagmi"
   "ngmi"
   "ser"
   "few understand"
   "this is alpha"
   "diamond hands"
   "hodl"
   "to the moon"
   "wen moon"
   "this is the way"
   
   ;; Short affirmations
   "yep"
   "yup"
   "yeah"
   "true"
   "indeed"
   "absolutely"
   "for sure"
   "definitely"
   "agreed"
   "same"
   "this"
   "^^"
   "^^^"
   "this ^"
   "came here to say this"
   "beat me to it"
   
   ;; Emoji-style (text representation)
   "🔥"
   "💯"
   "✨"
   "🚀"
   "💎"
   "🎯"
   "👏"
   "🙌"
   "❤️"
   "🔥🔥🔥"
   
   ;; Thoughtful responses
   "this is a good reminder"
   "important point"
   "worth considering"
   "something to think about"
   "this perspective helps"
   "appreciate the insight"
   "learning something new"
   "this changes things"
   "hadn't seen it this way"
   "eye opening"])

(def replies-level-5-10
  [;; Supportive and encouraging
   "i believe in you"
   "thats awesome"
   "you got this"
   "keep it up"
   "proud of you"
   "you're doing great"
   "this is amazing"
   "so cool"
   "love to see it"
   "this is fire"
   "you're killing it"
   "keep going"
   "this inspires me"
   "you're on fire"
   "absolutely crushing it"
   "this is the way"
   "legend"
   "goat"
   "you're the best"
   "this is incredible"
   
   ;; Positive reactions
   "hell yeah"
   "yes yes yes"
   "100% this"
   "exactly"
   "preach"
   "say it louder"
   "facts"
   "truth"
   "couldn't agree more"
   "this right here"
   "spot on"
   "nailed it"
   "perfect"
   "beautiful"
   "stunning"
   
   ;; Encouraging engagement
   "keep sharing"
   "more of this please"
   "need more content like this"
   "this is what we need"
   "keep it coming"
   "don't stop"
   "this is why i'm here"
   "exactly what i needed to hear"
   "this made my day"
   "needed this"
   
   ;; Short affirmations
   "this"
   "^^"
   "^^^"
   "this ^"
   "same"
   "yes"
   "yep"
   "absolutely"
   "for sure"
   "definitely"
   "agreed"
   "facts"
   "real"
   "based"])

(defn get-random-reply [player-level]
  "Get a random canned reply based on player level"
  (if (<= player-level 5)
    (rand-nth replies-level-1-5)
    (rand-nth replies-level-5-10)))

(defn get-replies-by-level [max-level]
  "Get all replies appropriate for levels up to max-level"
  (if (<= max-level 5)
    replies-level-1-5
    replies-level-5-10))

(defn library-stats []
  "Get statistics about the reply library"
  {:total-replies-level-1-5 (count replies-level-1-5)
   :total-replies-level-5-10 (count replies-level-5-10)
   :total-replies (+ (count replies-level-1-5) (count replies-level-5-10))})

;; Log library stats on load
(js/console.log "Reply library loaded:" (pr-str (library-stats)))

