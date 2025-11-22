(ns fc-idlegame.config)

;; ============================================================================
;; GAME CONFIGURATION & CONSTANTS
;; ============================================================================

;; XP & Leveling
(def xp-sources
  {:like-given 1         ;; Give a like to someone's cast (reduced XP, no cooldown)
   :reply-given 5        ;; Reply to someone's cast
   :cast-generated 1     ;; Generate your own cast (level 5+)
   :idle-cast 1          ;; Idle cast generated automatically
   :like-received 5      ;; Someone likes your cast
   :reply-received 3     ;; Someone replies to your cast
   :banger-achieved 100}) ;; Your cast becomes a banger

(def xp-per-level 100)

(defn level-from-xp [xp]
  "Calculate level from total XP
   Minimum level is 1 (players always start at level 1)"
  (max 1 (Math/floor (Math/sqrt (/ xp xp-per-level)))))

(defn xp-for-level [level]
  "Calculate XP needed to reach a level"
  (* level level xp-per-level))

(defn xp-to-next-level [current-xp]
  "Calculate XP needed for next level"
  (let [current-level (level-from-xp current-xp)
        next-level (inc current-level)
        next-level-xp (xp-for-level next-level)]
    (- next-level-xp current-xp)))

;; ============================================================================
;; RECHARGE METERS - SINGLE SOURCE OF TRUTH
;; ============================================================================
;; ⚠️ CHANGE THESE VALUES FOR TESTING - All recharge times are defined here
;; These are in milliseconds

(def ^:const RECHARGE-TIMES
  {:reply 5000       ;; Reply cooldown (5 seconds for testing)
   :cast 10000       ;; Manual cast cooldown (10 seconds for testing)
   :idle-cast 15000  ;; Idle cast interval (15 seconds for testing)
   ;; Note: Like recharge removed - no cooldown on likes
   })

;; Base recharge times (in milliseconds) - references RECHARGE-TIMES
(def base-recharge-times RECHARGE-TIMES)

;; Recharge time decreases with level
(defn get-recharge-time [action-type level modifiers]
  "Calculate recharge time based on level and modifiers
   Each level reduces recharge time by 2%"
  (let [base-time (get base-recharge-times action-type)
        level-multiplier (Math/pow 0.98 level)  ;; 2% reduction per level
        modifier-multiplier (or (:recharge-speed modifiers) 1.0)]
    (* base-time level-multiplier modifier-multiplier)))

;; ============================================================================
;; CAST SYSTEM
;; ============================================================================

;; Cast Quality Tiers - correlated to wit level
(def quality-tiers
  {:common {:weight 80 :name "Common" :emoji "⚪" :wit-level 1}
   :uncommon {:weight 15 :name "Uncommon" :emoji "🟢" :wit-level 2}
   :rare {:weight 4 :name "Rare" :emoji "🔵" :wit-level 3}
   :epic {:weight 0.9 :name "Epic" :emoji "🟣" :wit-level 4}
   :legendary {:weight 0.1 :name "Legendary" :emoji "🟡" :wit-level 5}})

(defn roll-quality []
  "Roll for cast quality based on weighted probabilities"
  (let [total-weight (reduce + (map (comp :weight second) quality-tiers))
        roll (rand total-weight)]
    (loop [remaining roll
           tiers (seq quality-tiers)]
      (if (empty? tiers)
        :common
        (let [[tier-key tier-data] (first tiers)
              weight (:weight tier-data)]
          (if (<= remaining weight)
            tier-key
            (recur (- remaining weight) (rest tiers))))))))

;; Cast Level Ranges - 1000 casts total, 100 per level range
(def cast-level-ranges
  [{:min-level 1 :max-level 1 :file "casts_level_01.cljs" :description "New to Farcaster, exploring, asking questions"}
   {:min-level 2 :max-level 2 :file "casts_level_02.cljs" :description "Learning basics, personal experiences"}
   {:min-level 3 :max-level 3 :file "casts_level_03.cljs" :description "Getting comfortable, finding voice"}
   {:min-level 4 :max-level 4 :file "casts_level_04.cljs" :description "Building confidence, more engagement"}
   {:min-level 5 :max-level 5 :file "casts_level_05.cljs" :description "Active participant, sharing opinions"}
   {:min-level 6 :max-level 6 :file "casts_level_06.cljs" :description "Understanding crypto basics, more sophisticated"}
   {:min-level 7 :max-level 7 :file "casts_level_07.cljs" :description "Crypto-aware, social dynamics understood"}
   {:min-level 8 :max-level 8 :file "casts_level_08.cljs" :description "Well-versed in community, good takes"}
   {:min-level 9 :max-level 9 :file "casts_level_09.cljs" :description "Established voice, quality content"}
   {:min-level 10 :max-level 10 :file "casts_level_10.cljs" :description "Expert takes, banger potential"}])

(defn get-available-cast-levels [player-level]
  "Get the level ranges available for cast generation
   Returns casts from (level - 2) to level, never exceeding player level"
  (let [min-level (max 1 (- player-level 2))
        max-level player-level]
    (filter #(and (>= (:max-level %) min-level)
                  (<= (:min-level %) max-level))
            cast-level-ranges)))

;; ============================================================================
;; LEVEL-BASED FEATURES
;; ============================================================================

(def level-features
  {1 {:can-cast? true            ;; Can cast from the start
      :can-like? true            ;; Can like
      :can-reply? true           ;; Can reply
      :can-share-level? true
      :can-cast-to-fc? false
      :ai-generation? false
      :memory-size 0}

   5 {:can-cast? true            ;; Casting still available
      :can-like? true
      :can-reply? true
      :can-share-level? true
      :can-cast-to-fc? false
      :ai-generation? false
      :memory-size 0}

   10 {:can-cast? true
       :can-like? true
       :can-reply? true
       :can-share-level? true
       :can-cast-to-fc? true     ;; Unlock cast to Farcaster!
       :can-create-bangers? true  ;; Can create bangers
       :ai-generation? false
       :memory-size 0}

   20 {:can-cast? true
       :can-like? true
       :can-reply? true
       :can-share-level? true
       :can-cast-to-fc? true
       :can-create-bangers? true
       :ai-generation? false
       :memory-size 20}

   30 {:can-cast? true
       :can-like? true
       :can-reply? true
       :can-share-level? true
       :can-cast-to-fc? true
       :can-create-bangers? true
       :ai-generation? true       ;; Unlock AI generation!
       :memory-size 50
       :ai-mix-percent 80}})

(defn get-level-features [level]
  "Get features available at a given level"
  (let [level (max 1 (or level 1))  ;; Ensure level is at least 1
        milestone-levels (sort (keys level-features))
        applicable-level (last (filter #(<= % level) milestone-levels))]
    (if (nil? applicable-level)
      ;; If no applicable level found (shouldn't happen with level >= 1), default to level 1 features
      (do
        (js/console.warn (str "⚠️ No applicable level found for level " level ", defaulting to level 1 features"))
        (get level-features 1
             {:can-cast? true
              :can-like? true
              :can-reply? true
              :can-share-level? true
              :can-cast-to-fc? false
              :can-create-bangers? false
              :ai-generation? false
              :memory-size 0}))
      (get level-features applicable-level
           {:can-cast? true  ;; Default to true instead of false
            :can-like? true
            :can-reply? true
            :can-share-level? true
            :can-cast-to-fc? false
            :can-create-bangers? false
            :ai-generation? false
            :memory-size 0}))))

;; ============================================================================
;; BANGER SYSTEM
;; ============================================================================

(def banger-threshold
  {:min-level 10          ;; Must be level 10+ to create bangers
   :required-likes 10     ;; 10 likes from real players (not NPCs)
   :one-like-per-user true}) ;; Each user can only like once

;; ============================================================================
;; FEED & SOCIAL
;; ============================================================================

(def feed-level-range 2) ;; Show casts from ±2 levels

;; NPC System - 5x rates for testing
(def npc-like-probability 0.75) ;; 75% chance per cast per tick (5x increase - was 15%)
(def npc-reply-probability 0.05) ;; 5% chance per tick
(def npc-tick-interval-ms 3000) ;; Check every 3 seconds (10x faster - was 30 seconds, gives ~5x NPC casts vs player casts)

;; Idle Cast System - uses RECHARGE-TIMES[:idle-cast] as single source of truth
(def idle-cast-interval-ms (:idle-cast RECHARGE-TIMES))

;; ============================================================================
;; CAST LIFESPAN
;; ============================================================================

(def cast-lifespan-ms
  {:level-1-10 (* 1000 60 60 2)     ;; 2 hours for early levels
   :level-11-20 (* 1000 60 60 8)    ;; 8 hours
   :level-21-30 (* 1000 60 60 24)   ;; 24 hours
   :banger nil})                     ;; Bangers never expire

(defn get-cast-lifespan [level is-banger?]
  "Get lifespan for a cast based on level"
  (if is-banger?
    nil
    (cond
      (<= level 10) (:level-1-10 cast-lifespan-ms)
      (<= level 20) (:level-11-20 cast-lifespan-ms)
      :else (:level-21-30 cast-lifespan-ms))))

;; ============================================================================
;; SHARE IMAGE CONFIGURATION (Vaporwave Style)
;; ============================================================================

(def share-image-config
  {:width 1200
   :height 630
   :background {:type "vaporwave-gradient"
                :colors ["#FF6EC7" "#7873F5" "#4FACFE"]  ;; Pink to purple to blue
                :angle 135}
   :text {:color "#FFFFFF"
          :stroke "#000000"
          :stroke-width 3
          :glow-color "#8E44AD"  ;; Farcaster purple
          :glow-blur 20
          :font-family "Arial Black, sans-serif"
          :max-chars 100}
   :stats-box {:background "rgba(0, 0, 0, 0.3)"
               :border "2px solid rgba(142, 68, 173, 0.8)"
               :padding 20
               :position {:x 50 :y 50}}
   :cast-display {:type "best-casts"  ;; Focus on best casts
                  :max-casts 3}})

;; ============================================================================
;; NFT CONFIGURATION
;; ============================================================================

(def nft-config
  {:chain "base"
   :creator-split 0.90
   :platform-fee 0.10
   :contract-address nil}) ;; To be set after deployment

;; ============================================================================
;; FIREBASE COLLECTIONS
;; ============================================================================

(def firebase-collections
  {:players "players"
   :casts "casts"
   :likes "likes"         ;; Track who liked what (prevent duplicates)
   :replies "replies"
   :bangers "bangers"
   :nfts "nfts"})

;; ============================================================================
;; AI GENERATION CONFIGURATION (Phase 3)
;; ============================================================================

(def ai-generation-config
  {:emulation-rarity-map
   {:low-match :uncommon      ;; Doesn't match player style well
    :medium-match :rare        ;; Somewhat matches
    :high-match :epic          ;; Good match
    :perfect-match :legendary} ;; Perfect emulation

   :legendary-cast-input true  ;; Use legendary canned casts as input
   :style-formatting true})    ;; Format in player's style

;; ============================================================================
;; API KEYS
;; ============================================================================

(def api-keys
  (let [process-env (when (and (not= js/process js/undefined)
                                (.-env js/process))
                      (.-env js/process))]
    {:openai-key (when process-env (.-OPENAI_API_KEY process-env))
     :firebase-config (when process-env (.-FIREBASE_CONFIG process-env))}))
