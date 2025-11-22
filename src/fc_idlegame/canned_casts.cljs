(ns fc-idlegame.canned-casts)

;; ============================================================================
;; CANNED CAST LIBRARY - Starter Set (300 casts)
;; Full library will be 1000 casts (100 per level)
;; Each cast has: text, level, rarity (correlates to wit)
;;
;; Level progression:
;; 1-3: New to Farcaster, learning, personal
;; 4-6: Understanding basics, building confidence
;; 7-9: Crypto-aware, good social dynamics
;; 10: Expert takes, banger potential
;; ============================================================================

(def canned-casts
  [;; ========================================================================
   ;; LEVEL 0 - Very first interactions, pre-account, most basic
   ;; ========================================================================

   ;; Common (dimwit) - Level 0
   {:text "test" :level 0 :rarity :common}
   {:text "hello" :level 0 :rarity :common}
   {:text "hi" :level 0 :rarity :common}
   {:text "hey" :level 0 :rarity :common}
   {:text "what is this" :level 0 :rarity :common}
   {:text "where am i" :level 0 :rarity :common}
   {:text "what is happening" :level 0 :rarity :common}
   {:text "confused" :level 0 :rarity :common}
   {:text "help" :level 0 :rarity :common}
   {:text "?" :level 0 :rarity :common}
   {:text "???" :level 0 :rarity :common}
   {:text "what" :level 0 :rarity :common}
   {:text "huh" :level 0 :rarity :common}
   {:text "ok" :level 0 :rarity :common}
   {:text "okay" :level 0 :rarity :common}
   {:text "cool" :level 0 :rarity :common}
   {:text "nice" :level 0 :rarity :common}
   {:text "weird" :level 0 :rarity :common}
   {:text "interesting" :level 0 :rarity :common}
   {:text "hmm" :level 0 :rarity :common}
   {:text "hm" :level 0 :rarity :common}
   {:text "idk" :level 0 :rarity :common}
   {:text "i dont know" :level 0 :rarity :common}
   {:text "maybe" :level 0 :rarity :common}
   {:text "sure" :level 0 :rarity :common}
   {:text "yeah" :level 0 :rarity :common}
   {:text "yep" :level 0 :rarity :common}
   {:text "nope" :level 0 :rarity :common}
   {:text "nah" :level 0 :rarity :common}
   {:text "lol" :level 0 :rarity :common}
   {:text "haha" :level 0 :rarity :common}
   {:text "lmao" :level 0 :rarity :common}
   {:text "wow" :level 0 :rarity :common}
   {:text "omg" :level 0 :rarity :common}
   {:text "wtf" :level 0 :rarity :common}
   {:text "bruh" :level 0 :rarity :common}
   {:text "fr" :level 0 :rarity :common}
   {:text "frfr" :level 0 :rarity :common}
   {:text "tbh" :level 0 :rarity :common}
   {:text "ngl" :level 0 :rarity :common}
   {:text "same" :level 0 :rarity :common}
   {:text "facts" :level 0 :rarity :common}

   ;; Uncommon - Level 0
   {:text "what even is this place" :level 0 :rarity :uncommon}
   {:text "someone explain what im looking at" :level 0 :rarity :uncommon}
   {:text "this seems different from other apps" :level 0 :rarity :uncommon}
   {:text "curious what this is about" :level 0 :rarity :uncommon}
   {:text "heard this was cool" :level 0 :rarity :uncommon}
   {:text "trying to understand" :level 0 :rarity :uncommon}
   {:text "seems interesting so far" :level 0 :rarity :uncommon}
   {:text "not sure what to make of this" :level 0 :rarity :uncommon}
   {:text "early impressions are positive" :level 0 :rarity :uncommon}
   {:text "feels different somehow" :level 0 :rarity :uncommon}

   ;; Rare - Level 0
   {:text "the interface is clean but i'm lost" :level 0 :rarity :rare}
   {:text "wondering if this is worth my time" :level 0 :rarity :rare}
   {:text "first time trying something like this" :level 0 :rarity :rare}
   {:text "taking a leap into the unknown" :level 0 :rarity :rare}
   {:text "curious about the community here" :level 0 :rarity :rare}
   {:text "seems like there's potential here" :level 0 :rarity :rare}
   {:text "early days but i'm intrigued" :level 0 :rarity :rare}
   {:text "willing to give this a chance" :level 0 :rarity :rare}

   ;; Epic - Level 0
   {:text "stepping into uncharted territory, let's see what happens" :level 0 :rarity :epic}
   {:text "every new platform starts somewhere, maybe this is it" :level 0 :rarity :epic}
   {:text "the unknown is where interesting things happen" :level 0 :rarity :epic}
   {:text "first impressions matter but so does giving things time" :level 0 :rarity :epic}

   ;; ========================================================================
   ;; LEVEL 1 - New to Farcaster, exploring, asking questions
   ;; ========================================================================

   ;; Common (dimwit) - Level 1
   {:text "hi everyone" :level 1 :rarity :common}
   {:text "what is this app" :level 1 :rarity :common}
   {:text "how do i use this" :level 1 :rarity :common}
   {:text "testing 123" :level 1 :rarity :common}
   {:text "first post" :level 1 :rarity :common}
   {:text "is anyone here" :level 1 :rarity :common}
   {:text "this is cool i think" :level 1 :rarity :common}
   {:text "what do people do here" :level 1 :rarity :common}
   {:text "can someone explain this" :level 1 :rarity :common}
   {:text "im new lol" :level 1 :rarity :common}
   {:text "hello world" :level 1 :rarity :common}
   {:text "trying this out" :level 1 :rarity :common}
   {:text "not sure what im doing" :level 1 :rarity :common}
   {:text "someone help me" :level 1 :rarity :common}
   {:text "this is different" :level 1 :rarity :common}
   {:text "okay im here now what" :level 1 :rarity :common}
   {:text "downloaded this because of twitter" :level 1 :rarity :common}
   {:text "whats the point of this" :level 1 :rarity :common}
   {:text "just joined" :level 1 :rarity :common}
   {:text "hi" :level 1 :rarity :common}
   {:text "hey" :level 1 :rarity :common}
   {:text "whats up" :level 1 :rarity :common}
   {:text "new here" :level 1 :rarity :common}
   {:text "first day" :level 1 :rarity :common}
   {:text "how does this work" :level 1 :rarity :common}
   {:text "confused" :level 1 :rarity :common}
   {:text "help" :level 1 :rarity :common}
   {:text "anyone around" :level 1 :rarity :common}
   {:text "is this like twitter" :level 1 :rarity :common}
   {:text "where are the likes" :level 1 :rarity :common}
   {:text "how do i post" :level 1 :rarity :common}
   {:text "what should i post" :level 1 :rarity :common}
   {:text "this seems cool" :level 1 :rarity :common}
   {:text "still figuring it out" :level 1 :rarity :common}
   {:text "learning" :level 1 :rarity :common}
   {:text "exploring" :level 1 :rarity :common}
   {:text "checking this out" :level 1 :rarity :common}
   {:text "heard about this" :level 1 :rarity :common}
   {:text "friend told me to join" :level 1 :rarity :common}
   {:text "came from reddit" :level 1 :rarity :common}
   {:text "came from discord" :level 1 :rarity :common}
   {:text "looking for something new" :level 1 :rarity :common}
   {:text "tired of twitter" :level 1 :rarity :common}
   {:text "tired of facebook" :level 1 :rarity :common}
   {:text "need a break from social media" :level 1 :rarity :common}
   {:text "what makes this special" :level 1 :rarity :common}
   {:text "why should i use this" :level 1 :rarity :common}
   {:text "seems empty" :level 1 :rarity :common}
   {:text "where is everyone" :level 1 :rarity :common}
   {:text "small community" :level 1 :rarity :common}
   {:text "nice people here" :level 1 :rarity :common}
   {:text "friendly place" :level 1 :rarity :common}
   {:text "better than expected" :level 1 :rarity :common}
   {:text "not what i expected" :level 1 :rarity :common}
   {:text "interesting" :level 1 :rarity :common}
   {:text "weird but cool" :level 1 :rarity :common}
   {:text "giving it a shot" :level 1 :rarity :common}
   {:text "we'll see how this goes" :level 1 :rarity :common}

   ;; Uncommon - Level 1
   {:text "curious about this decentralized thing everyone talks about" :level 1 :rarity :uncommon}
   {:text "coming from twitter, what makes this different?" :level 1 :rarity :uncommon}
   {:text "okay so no algorithm means what exactly" :level 1 :rarity :uncommon}
   {:text "someone told me this is the future of social media" :level 1 :rarity :uncommon}
   {:text "excited to learn about web3 social" :level 1 :rarity :uncommon}

   ;; Rare - Level 1
   {:text "fascinated by the idea of owning my social graph" :level 1 :rarity :rare}
   {:text "if i control my data here, does that mean i can take it elsewhere?" :level 1 :rarity :rare}
   {:text "the tech behind this seems interesting, want to understand more" :level 1 :rarity :rare}

   ;; Epic - Level 1
   {:text "first impression: cleaner signal than twitter, but where's everyone?" :level 1 :rarity :epic}
   {:text "learning by doing - that's the way with new protocols" :level 1 :rarity :epic}

   ;; ========================================================================
   ;; LEVEL 2 - Learning basics, personal experiences
   ;; ========================================================================

   ;; Common - Level 2
   {:text "having coffee" :level 2 :rarity :common}
   {:text "good morning" :level 2 :rarity :common}
   {:text "what are you working on" :level 2 :rarity :common}
   {:text "this platform is growing on me" :level 2 :rarity :common}
   {:text "need more followers" :level 2 :rarity :common}
   {:text "how do i get more visibility" :level 2 :rarity :common}
   {:text "still learning how this works" :level 2 :rarity :common}
   {:text "who should i follow" :level 2 :rarity :common}
   {:text "liking the vibe here" :level 2 :rarity :common}
   {:text "way better than twitter" :level 2 :rarity :common}
   {:text "actually having conversations here" :level 2 :rarity :common}
   {:text "people seem nicer here" :level 2 :rarity :common}
   {:text "what does everyone do for work" :level 2 :rarity :common}
   {:text "anyone else here from twitter" :level 2 :rarity :common}
   {:text "this is my second week here" :level 2 :rarity :common}
   {:text "getting the hang of it" :level 2 :rarity :common}
   {:text "the community seems tight knit" :level 2 :rarity :common}
   {:text "is there a way to see trending topics" :level 2 :rarity :common}
   {:text "made my first friend on here today" :level 2 :rarity :common}
   {:text "loving the early adopter energy" :level 2 :rarity :common}

   ;; Uncommon - Level 2
   {:text "starting to understand why people care about decentralization" :level 2 :rarity :uncommon}
   {:text "the lack of ads is refreshing" :level 2 :rarity :uncommon}
   {:text "interesting how different the conversations are without likes being the main metric" :level 2 :rarity :uncommon}
   {:text "engagement feels more genuine here" :level 2 :rarity :uncommon}
   {:text "taking time to understand the culture before posting too much" :level 2 :rarity :uncommon}

   ;; Rare - Level 2
   {:text "the composability of this protocol is what drew me in" :level 2 :rarity :rare}
   {:text "building in public feels different when you own your audience" :level 2 :rarity :rare}
   {:text "interesting tension between growth and maintaining quality" :level 2 :rarity :rare}

   ;; Epic - Level 2
   {:text "watching how micro-communities form here is fascinating from a social dynamics perspective" :level 2 :rarity :epic}
   {:text "early platforms always reward those who contribute to culture, not just consume" :level 2 :rarity :epic}

   ;; ========================================================================
   ;; LEVEL 3 - Getting comfortable, finding voice
   ;; ========================================================================

   ;; Common - Level 3
   {:text "finally feeling like i belong here" :level 3 :rarity :common}
   {:text "shoutout to everyone who welcomed me" :level 3 :rarity :common}
   {:text "excited for what's next" :level 3 :rarity :common}
   {:text "anyone want to connect" :level 3 :rarity :common}
   {:text "what are your favorite channels" :level 3 :rarity :common}
   {:text "started posting regularly" :level 3 :rarity :common}
   {:text "getting some good engagement" :level 3 :rarity :common}
   {:text "this place is special" :level 3 :rarity :common}
   {:text "never going back to twitter" :level 3 :rarity :common}
   {:text "found my people" :level 3 :rarity :common}
   {:text "the vibes are immaculate" :level 3 :rarity :common}
   {:text "love how supportive everyone is" :level 3 :rarity :common}
   {:text "actually enjoying social media again" :level 3 :rarity :common}
   {:text "best decision to join early" :level 3 :rarity :common}
   {:text "quality over quantity" :level 3 :rarity :common}
   {:text "small community but mighty" :level 3 :rarity :common}
   {:text "everyone here is building something" :level 3 :rarity :common}
   {:text "the energy is different" :level 3 :rarity :common}
   {:text "so many smart people in one place" :level 3 :rarity :common}
   {:text "grateful to be here" :level 3 :rarity :common}

   ;; Uncommon - Level 3
   {:text "finding my niche in the crypto art community here" :level 3 :rarity :uncommon}
   {:text "the signal to noise ratio is what keeps me coming back" :level 3 :rarity :uncommon}
   {:text "experimenting with different types of content to see what resonates" :level 3 :rarity :uncommon}
   {:text "turns out consistency matters more than virality here" :level 3 :rarity :uncommon}
   {:text "the long-form discussion threads are where the gold is" :level 3 :rarity :uncommon}

   ;; Rare - Level 3
   {:text "reputation systems that aren't just follower counts are the future" :level 3 :rarity :rare}
   {:text "watching how projects coordinate on farcaster vs twitter is night and day" :level 3 :rarity :rare}
   {:text "the composability here means your social capital is actually portable" :level 3 :rarity :rare}

   ;; Epic - Level 3
   {:text "early platform dynamics: high trust, strong reciprocity, exponential network effects for contributors" :level 3 :rarity :epic}
   {:text "the lack of algorithmic amplification reveals who actually provides value vs who games metrics" :level 3 :rarity :epic}

   ;; ========================================================================
   ;; LEVEL 4 - Building confidence, more engagement
   ;; ========================================================================

   ;; Common - Level 4
   {:text "good conversation today about nfts" :level 4 :rarity :common}
   {:text "learning so much from this community" :level 4 :rarity :common}
   {:text "everyone should join farcaster" :level 4 :rarity :common}
   {:text "sharing my first project soon" :level 4 :rarity :common}
   {:text "the alpha is in the replies" :level 4 :rarity :common}
   {:text "got into crypto because of farcaster" :level 4 :rarity :common}
   {:text "finally understand what web3 social means" :level 4 :rarity :common}
   {:text "the community vibe is unmatched" :level 4 :rarity :common}
   {:text "started a channel for my interests" :level 4 :rarity :common}
   {:text "collaborating with other builders" :level 4 :rarity :common}
   {:text "shipped my first mini app" :level 4 :rarity :common}
   {:text "this is how social should work" :level 4 :rarity :common}
   {:text "ownership changes everything" :level 4 :rarity :common}
   {:text "excited for mainnet" :level 4 :rarity :common}
   {:text "farcaster summer incoming" :level 4 :rarity :common}
   {:text "we're all gonna make it" :level 4 :rarity :common}
   {:text "the builders here are insane" :level 4 :rarity :common}
   {:text "so many good projects launching" :level 4 :rarity :common}
   {:text "farcaster native feels different" :level 4 :rarity :common}
   {:text "this is just the beginning" :level 4 :rarity :common}

   ;; Uncommon - Level 4
   {:text "interesting how channels create micro-economies of attention" :level 4 :rarity :uncommon}
   {:text "the permissionless nature of building here is wild" :level 4 :rarity :uncommon}
   {:text "frames changed my entire perception of what's possible" :level 4 :rarity :uncommon}
   {:text "watching new use cases emerge in real-time" :level 4 :rarity :uncommon}
   {:text "the developer community here is top tier" :level 4 :rarity :uncommon}

   ;; Rare - Level 4
   {:text "farcaster as infrastructure for onchain social graphs is underrated" :level 4 :rarity :rare}
   {:text "the data portability isn't just a feature, it's a moat against platform risk" :level 4 :rarity :rare}
   {:text "building with sufficiently decentralized means you can take real risks" :level 4 :rarity :rare}

   ;; Epic - Level 4
   {:text "farcaster's success will be measured by how many successful social apps get built on top, not users" :level 4 :rarity :epic}
   {:text "we're speedrunning the evolution of social media in a permissionless environment" :level 4 :rarity :epic}

   ;; ========================================================================
   ;; LEVEL 5 - Active participant, sharing opinions
   ;; ========================================================================

   ;; Common - Level 5
   {:text "hot take: farcaster is already better than twitter" :level 5 :rarity :common}
   {:text "the best content is in niche channels" :level 5 :rarity :common}
   {:text "frames are the future of social apps" :level 5 :rarity :common}
   {:text "onchain social graphs are inevitable" :level 5 :rarity :common}
   {:text "we're so early" :level 5 :rarity :common}
   {:text "the protocol is the platform" :level 5 :rarity :common}
   {:text "composability unlocks creativity" :level 5 :rarity :common}
   {:text "farcaster native projects hit different" :level 5 :rarity :common}
   {:text "building in public on farcaster just feels right" :level 5 :rarity :common}
   {:text "the signal is immaculate today" :level 5 :rarity :common}
   {:text "reputation is the new social currency" :level 5 :rarity :common}
   {:text "few understand where this is going" :level 5 :rarity :common}
   {:text "the vibe shift is real" :level 5 :rarity :common}
   {:text "onchain is the only way forward" :level 5 :rarity :common}
   {:text "social apps should be composable by default" :level 5 :rarity :common}
   {:text "we're building the future of the internet" :level 5 :rarity :common}
   {:text "every day there's something new to discover" :level 5 :rarity :common}
   {:text "the community keeps getting better" :level 5 :rarity :common}
   {:text "farcaster taught me to think differently about social" :level 5 :rarity :common}
   {:text "decentralization isn't just a buzzword here" :level 5 :rarity :common}

   ;; Uncommon - Level 5
   {:text "the transition from web2 to web3 social isn't about features, it's about ownership" :level 5 :rarity :uncommon}
   {:text "channels solve the cold start problem in a way twitter lists never could" :level 5 :rarity :uncommon}
   {:text "farcaster's architecture allows for experimentation at the client layer" :level 5 :rarity :uncommon}
   {:text "interesting how economic incentives shape content quality here" :level 5 :rarity :uncommon}
   {:text "the composability creates unexpected emergent behaviors" :level 5 :rarity :uncommon}

   ;; Rare - Level 5
   {:text "farcaster is proof that sufficiently decentralized infrastructure enables permissionless innovation" :level 5 :rarity :rare}
   {:text "the protocol's constraint of immutability forces better thinking about content moderation" :level 5 :rarity :rare}
   {:text "watching the social graph become a public good in real-time is fascinating" :level 5 :rarity :rare}

   ;; Epic - Level 5
   {:text "farcaster's innovation isn't the protocol, it's the separation of identity, content, and presentation layers" :level 5 :rarity :epic}
   {:text "the network effects here compound differently because users own their connections" :level 5 :rarity :epic}

   ;; ========================================================================
   ;; More levels will be added in separate library generation session
   ;; Target: 1000 casts total (100 per level for levels 1-10)
   ;; ========================================================================
   ])

(def cast-count (count canned-casts))

;; ============================================================================
;; CAST RETRIEVAL FUNCTIONS
;; ============================================================================

(defn get-casts-by-level [level]
  "Get all casts for a specific level"
  (filter #(= (:level %) level) canned-casts))

(defn get-casts-by-level-range [min-level max-level]
  "Get casts within a level range (inclusive)"
  (filter #(and (>= (:level %) min-level)
                (<= (:level %) max-level))
          canned-casts))

(defn get-casts-by-rarity [rarity]
  "Get all casts of a specific rarity"
  (filter #(= (:rarity %) rarity) canned-casts))

(defn get-cast-for-player [player-level target-rarity]
  "Get a random cast appropriate for player's level and desired rarity
   Selects from last 3 levels worth of content (never exceeding player level, minimum level 1)"
  (let [min-level (max 1 (- player-level 2))  ;; Minimum level 1, never go below
        max-level player-level
        available-casts (filter #(and (>= (:level %) min-level)
                                      (<= (:level %) max-level)
                                      (= (:rarity %) target-rarity))
                                canned-casts)]
    (when (seq available-casts)
      (rand-nth available-casts))))

(defn get-random-cast-for-level [player-level]
  "Get a random cast appropriate for player level, with quality rolled
   Minimum level 1, never go below"
  ;; Roll for quality first
  (let [min-level (max 1 (- player-level 2))  ;; Minimum level 1, never go below
        max-level player-level
        available-casts (get-casts-by-level-range min-level max-level)]
    (when (seq available-casts)
      (rand-nth available-casts))))

;; ============================================================================
;; LIBRARY STATS
;; ============================================================================

(defn library-stats []
  "Get statistics about the cast library"
  (let [by-level (group-by :level canned-casts)
        by-rarity (group-by :rarity canned-casts)]
    {:total-casts cast-count
     :casts-by-level (into {} (map (fn [[k v]] [k (count v)]) by-level))
     :casts-by-rarity (into {} (map (fn [[k v]] [k (count v)]) by-rarity))
     :coverage {:min-level (apply min (map :level canned-casts))
                :max-level (apply max (map :level canned-casts))}}))

;; Log library stats on load
(js/console.log "Cast library loaded:" (pr-str (library-stats)))
