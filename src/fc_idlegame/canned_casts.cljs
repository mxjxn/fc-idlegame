(ns fc-idlegame.canned-casts)

;; ============================================================================
;; CANNED CAST LIBRARY
;; 100 pre-written casts organized by category
;; ============================================================================

(def canned-casts
  ;; Philosophical & Deep Thoughts (20)
  [{:text "sometimes the best reply is no reply at all" :category :philosophical}
   {:text "we're all just vibing in the same timeline" :category :philosophical}
   {:text "the algorithm knows but it's not telling" :category :philosophical}
   {:text "what if onchain is actually the friends we made along the way" :category :philosophical}
   {:text "decentralization is a state of mind" :category :philosophical}
   {:text "gm but make it existential" :category :philosophical}
   {:text "the real alpha was inside us all along" :category :philosophical}
   {:text "touching grass is just touching a different timeline" :category :philosophical}
   {:text "we're all NPCs in someone else's feed" :category :philosophical}
   {:text "perhaps the real airdrop is community" :category :philosophical}
   {:text "the void posts back sometimes" :category :philosophical}
   {:text "chronically online but spiritually offchain" :category :philosophical}
   {:text "shitposting as a form of meditation" :category :philosophical}
   {:text "consensus mechanisms for the soul" :category :philosophical}
   {:text "every cast is a merkle tree of thoughts" :category :philosophical}
   {:text "the timeline is a flat circle" :category :philosophical}
   {:text "proof of work but for good vibes" :category :philosophical}
   {:text "we're all just smart contracts trying our best" :category :philosophical}
   {:text "maybe the real gas fees were the experiences we had" :category :philosophical}
   {:text "existing onchain is its own reward" :category :philosophical}

   ;; Crypto/Web3 Culture (20)
   {:text "few understand this" :category :crypto}
   {:text "ngmi but respectfully" :category :crypto}
   {:text "probably nothing" :category :crypto}
   {:text "ser this is a wendy's" :category :crypto}
   {:text "wen token" :category :crypto}
   {:text "the prophecy foretold this" :category :crypto}
   {:text "bullish on literally everything" :category :crypto}
   {:text "number go up technology" :category :crypto}
   {:text "not financial advice but also kind of financial advice" :category :crypto}
   {:text "this is good for bitcoin" :category :crypto}
   {:text "vibe check: immaculate" :category :crypto}
   {:text "the charts are speaking to me" :category :crypto}
   {:text "price discovery is a journey" :category :crypto}
   {:text "zoom out (but not too far)" :category :crypto}
   {:text "we're so early it's still yesterday" :category :crypto}
   {:text "the devs are cooking" :category :crypto}
   {:text "reputation is the new liquidity" :category :crypto}
   {:text "peer to peer legend behavior" :category :crypto}
   {:text "decentralize all the things" :category :crypto}
   {:text "this changes everything (it does not)" :category :crypto}

   ;; Humor & Shitposting (20)
   {:text "i have no idea what i'm doing and that's okay" :category :humor}
   {:text "log off? never heard of her" :category :humor}
   {:text "main character energy but make it humble" :category :humor}
   {:text "this cast was written by committee" :category :humor}
   {:text "sorry i'm late i was doing hot girl shit (debugging)" :category :humor}
   {:text "normalize not having an opinion on everything" :category :humor}
   {:text "brain empty head full of memes" :category :humor}
   {:text "entrepreneur? i barely know her" :category :humor}
   {:text "my other timeline is a lamborghini" :category :humor}
   {:text "powered by spite and caffeine" :category :humor}
   {:text "local shitposter makes good occasionally" :category :humor}
   {:text "no thoughts just vibes" :category :humor}
   {:text "sir this is a protocol" :category :humor}
   {:text "rare W for team chaos" :category :humor}
   {:text "telling on myself for engagement" :category :humor}
   {:text "i'm not online i'm ONLINE" :category :humor}
   {:text "professional overthinker" :category :humor}
   {:text "context? we don't do that here" :category :humor}
   {:text "midwit take incoming" :category :humor}
   {:text "unhinged but in a productive way" :category :humor}

   ;; Relatable/Slice of Life (20)
   {:text "just remembered i exist" :category :relatable}
   {:text "coffee hits different when you're building" :category :relatable}
   {:text "taking a break from taking a break" :category :relatable}
   {:text "the grind never stops (yes it does)" :category :relatable}
   {:text "working on something cool (i think)" :category :relatable}
   {:text "productive day: defined three problems" :category :relatable}
   {:text "inbox zero is a lie we tell ourselves" :category :relatable}
   {:text "touching grass speedrun any%" :category :relatable}
   {:text "meeting that could have been a cast" :category :relatable}
   {:text "debugging my entire life rn" :category :relatable}
   {:text "main character of my own side quest" :category :relatable}
   {:text "found a bug (it was me)" :category :relatable}
   {:text "learned something new today (already forgot)" :category :relatable}
   {:text "current status: vibing" :category :relatable}
   {:text "slowly becoming the person i pretend to be online" :category :relatable}
   {:text "reading documentation (lying)" :category :relatable}
   {:text "refactoring my entire personality" :category :relatable}
   {:text "working in public on working in public" :category :relatable}
   {:text "just shipped (to staging)" :category :relatable}
   {:text "paralyzed by possibility" :category :relatable}

   ;; Inspirational/Wholesome (20)
   {:text "your progress is valid even if it's small" :category :wholesome}
   {:text "someone needed to see this today (it's me)" :category :wholesome}
   {:text "proud of everyone showing up and trying" :category :wholesome}
   {:text "slow progress is still progress" :category :wholesome}
   {:text "you're doing better than you think" :category :wholesome}
   {:text "community > competition" :category :wholesome}
   {:text "grateful for this weird corner of the internet" :category :wholesome}
   {:text "learning in public is an act of courage" :category :wholesome}
   {:text "your vibe attracts your tribe" :category :wholesome}
   {:text "start before you're ready" :category :wholesome}
   {:text "be kind (it's free)" :category :wholesome}
   {:text "everyone's fighting battles you can't see" :category :wholesome}
   {:text "the internet is better because you're in it" :category :wholesome}
   {:text "comparison is the thief of joy" :category :wholesome}
   {:text "done is better than perfect" :category :wholesome}
   {:text "your weird is your superpower" :category :wholesome}
   {:text "small wins compound" :category :wholesome}
   {:text "rest is productive too" :category :wholesome}
   {:text "you belong here" :category :wholesome}
   {:text "keep going you're closer than you think" :category :wholesome}])

(def cast-count (count canned-casts))

(defn get-random-cast []
  "Get a random canned cast"
  (rand-nth canned-casts))

(defn get-random-casts [n]
  "Get n random unique canned casts"
  (take n (shuffle canned-casts)))

(defn get-casts-by-category [category]
  "Get all casts from a specific category"
  (filter #(= (:category %) category) canned-casts))

(defn get-random-cast-by-category [category]
  "Get a random cast from a specific category"
  (rand-nth (get-casts-by-category category)))
