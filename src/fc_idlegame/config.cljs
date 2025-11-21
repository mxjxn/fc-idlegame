(ns fc-idlegame.config)

;; ============================================================================
;; GAME CONFIGURATION & CONSTANTS
;; ============================================================================

;; XP & Leveling
(def xp-sources
  {:manual-click 1
   :idle-cast 0.5
   :like-received 5
   :banger-achieved 100})

(def xp-per-level 100)

(defn level-from-xp [xp]
  "Calculate level from total XP"
  (Math/floor (Math/sqrt (/ xp xp-per-level))))

(defn xp-for-level [level]
  "Calculate XP needed to reach a level"
  (* level level xp-per-level))

(defn xp-to-next-level [current-xp]
  "Calculate XP needed for next level"
  (let [current-level (level-from-xp current-xp)
        next-level (inc current-level)
        next-level-xp (xp-for-level next-level)]
    (- next-level-xp current-xp)))

;; Progression Timeline
;; 10 levels in 1 month without manual participation
;; Level 10 = XP 10,000 = 20,000 idle casts = ~14 casts/hour for 30 days
;; With 2hrs/day at 120 clicks/min for 7 days = 100,800 clicks = 100,800 XP = ~Level 31

(def idle-cast-interval-ms
  "How often idle casts are generated (in milliseconds)"
  250000) ;; ~4 minutes per cast

(def clicks-per-cast 1)

;; Cast Quality Tiers
(def quality-tiers
  {:common {:weight 80 :name "Common" :emoji "⚪"}
   :uncommon {:weight 15 :name "Uncommon" :emoji "🟢"}
   :rare {:weight 4 :name "Rare" :emoji "🔵"}
   :epic {:weight 0.9 :name "Epic" :emoji "🟣"}
   :legendary {:weight 0.1 :name "Legendary" :emoji "🟡"}})

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

;; Level-based Features
(def level-features
  {1 {:can-share-level? true
      :can-cast-to-fc? false
      :ai-generation? false
      :memory-size 0}

   10 {:can-share-level? true
       :can-cast-to-fc? false
       :ai-generation? false
       :memory-size 0}

   11 {:can-share-level? true
       :can-cast-to-fc? false
       :ai-generation? true
       :memory-size 10
       :ai-mix-percent 30}  ;; 30% AI, 70% canned

   20 {:can-share-level? true
       :can-cast-to-fc? false
       :ai-generation? true
       :memory-size 20
       :ai-mix-percent 50}

   30 {:can-share-level? true
       :can-cast-to-fc? true
       :ai-generation? true
       :memory-size 50
       :ai-mix-percent 80}})

(defn get-level-features [level]
  "Get features available at a given level"
  (let [milestone-levels (sort (keys level-features))
        applicable-level (last (filter #(<= % level) milestone-levels))]
    (get level-features applicable-level
         {:can-share-level? true
          :can-cast-to-fc? false
          :ai-generation? false
          :memory-size 0})))

;; Banger System
(def banger-threshold
  {:total-likes 10
   :min-real-likes 5})

;; Feed System
(def feed-level-range 2) ;; Show casts from ±2 levels

;; NPC System
(def npc-like-probability 0.10) ;; 10% chance per tick
(def npc-tick-interval-ms 30000) ;; Check every 30 seconds

;; Cast Lifespan
(def cast-lifespan-ms
  {:level-1-10 (* 1000 60 60 1)     ;; 1 hour for early levels
   :level-11-20 (* 1000 60 60 6)    ;; 6 hours
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

;; Share Image Configuration
(def share-image-config
  {:width 1200
   :height 630
   :background-gradient {:start "#667eea" :end "#764ba2"}
   :stats-box {:width 300
               :height 120
               :opacity 0.85
               :position {:x 50 :y 50}}
   :cast-text {:max-chars 100
               :font-size 32
               :rotation-angles [-5 0 5]}})

;; NFT Configuration
(def nft-config
  {:chain "base"
   :creator-split 0.90
   :platform-fee 0.10
   :contract-address nil}) ;; To be set after deployment

;; Firebase Collections
(def firebase-collections
  {:players "players"
   :casts "casts"
   :likes "likes"
   :replies "replies"
   :bangers "bangers"
   :nfts "nfts"})

;; API Keys (to be set via environment)
(def api-keys
  {:openai-key (or (.-OPENAI_API_KEY js/process.env) nil)
   :firebase-config (or (.-FIREBASE_CONFIG js/process.env) nil)})
