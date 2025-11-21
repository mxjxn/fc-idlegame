(ns fc-idlegame.game
  (:require [clojure.string :as str]
            [fc-idlegame.config :as config]
            [fc-idlegame.canned-casts :as canned]))

;; ============================================================================
;; GAME STATE & DATA MODELS
;; ============================================================================

(def initial-game-state
  {:player {:level 1
            :xp 0
            :username "anon"
            :fid nil
            :display-name "Anonymous Player"
            :stats {:total-likes-given 0
                    :total-replies-given 0
                    :total-casts 0
                    :total-likes-received 0
                    :total-replies-received 0
                    :total-bangers 0
                    :nfts-minted 0
                    :game-time 0}
            :modifiers {:recharge-speed 1.0}}  ;; Multiplier for recharge speed

   :recharge-meters {:like {:last-action 0
                            :ready? true}
                     :reply {:last-action 0
                             :ready? true}
                     :cast {:last-action 0
                            :ready? true}}

   :casts []  ;; All casts (player + NPC)

   :likes {}  ;; Map of {cast-id #{user-fids-who-liked}}

   :feed {:visible-casts []
          :last-refresh 0}

   :game-loop {:last-tick (.now js/Date)
               :last-idle-cast (.now js/Date)
               :last-npc-tick (.now js/Date)}})

;; ============================================================================
;; HELPER FUNCTIONS
;; ============================================================================

(defn uuid []
  "Generate a simple UUID"
  (str "cast-" (.now js/Date) "-" (rand-int 99999)))

(defn now []
  "Get current timestamp"
  (.now js/Date))

(defn format-number [n]
  "Format numbers for display"
  (cond
    (>= n 1000000) (str (Math/floor (/ n 1000000)) "M")
    (>= n 1000) (str (Math/floor (/ n 1000)) "K")
    :else (str (Math/floor n))))

(defn format-time [seconds]
  "Format time for display"
  (let [hours (Math/floor (/ seconds 3600))
        minutes (Math/floor (/ (mod seconds 3600) 60))
        secs (Math/floor (mod seconds 60))]
    (str hours "h " minutes "m " secs "s")))

(defn format-recharge-time [ms]
  "Format milliseconds remaining as Xs"
  (let [seconds (Math/ceil (/ ms 1000))]
    (str seconds "s")))

;; ============================================================================
;; XP & LEVELING SYSTEM
;; ============================================================================

(defn add-xp [game-state amount reason]
  "Add XP to player and update level"
  (let [current-xp (get-in game-state [:player :xp])
        new-xp (+ current-xp amount)
        old-level (config/level-from-xp current-xp)
        new-level (config/level-from-xp new-xp)
        leveled-up? (> new-level old-level)]

    (js/console.log (str "XP +" amount " (" reason ") - Level " new-level))

    (cond-> game-state
      true (assoc-in [:player :xp] new-xp)
      true (assoc-in [:player :level] new-level)
      leveled-up? (as-> $ (do
                            (js/console.log (str "🎉 LEVEL UP! Now level " new-level))
                            $)))))

(defn get-current-level [game-state]
  "Get player's current level"
  (get-in game-state [:player :level]))

(defn get-xp-progress [game-state]
  "Get XP progress to next level as percentage"
  (let [current-xp (get-in game-state [:player :xp])
        current-level (config/level-from-xp current-xp)
        current-level-xp (config/xp-for-level current-level)
        next-level-xp (config/xp-for-level (inc current-level))
        progress-xp (- current-xp current-level-xp)
        needed-xp (- next-level-xp current-level-xp)]
    (* 100 (/ progress-xp needed-xp))))

;; ============================================================================
;; RECHARGE METER SYSTEM
;; ============================================================================

(defn get-recharge-progress [game-state action-type]
  "Get recharge progress (0-1) for an action"
  (let [level (get-current-level game-state)
        modifiers (get-in game-state [:player :modifiers])
        recharge-time (config/get-recharge-time action-type level modifiers)
        last-action (get-in game-state [:recharge-meters action-type :last-action])
        time-elapsed (- (now) last-action)
        progress (min 1.0 (/ time-elapsed recharge-time))]
    progress))

(defn is-action-ready? [game-state action-type]
  "Check if an action's recharge meter is full"
  (>= (get-recharge-progress game-state action-type) 1.0))

(defn get-time-until-ready [game-state action-type]
  "Get milliseconds until action is ready"
  (let [level (get-current-level game-state)
        modifiers (get-in game-state [:player :modifiers])
        recharge-time (config/get-recharge-time action-type level modifiers)
        last-action (get-in game-state [:recharge-meters action-type :last-action])
        time-elapsed (- (now) last-action)
        time-remaining (- recharge-time time-elapsed)]
    (max 0 time-remaining)))

(defn update-recharge-meters [game-state]
  "Update ready status for all recharge meters"
  (-> game-state
      (assoc-in [:recharge-meters :like :ready?] (is-action-ready? game-state :like))
      (assoc-in [:recharge-meters :reply :ready?] (is-action-ready? game-state :reply))
      (assoc-in [:recharge-meters :cast :ready?] (is-action-ready? game-state :cast))))

;; ============================================================================
;; CAST GENERATION SYSTEM
;; ============================================================================

(defn generate-cast-text [game-state]
  "Generate cast text based on level and rolled rarity
   Uses canned cast library with level/rarity matching"
  (let [level (get-current-level game-state)
        rolled-rarity (config/roll-quality)
        cast-data (canned/get-cast-for-player level rolled-rarity)]

    (if cast-data
      cast-data
      ;; Fallback if no cast found for that level/rarity combo
      (let [fallback (canned/get-random-cast-for-level level)]
        (or fallback {:text "vibing" :level level :rarity :common})))))

(defn create-cast [game-state creator-fid is-npc?]
  "Create a new cast"
  (let [level (if is-npc?
                (get-current-level game-state)  ;; NPCs match player level
                (get-current-level game-state))
        cast-data (generate-cast-text game-state)
        cast-text (:text cast-data)
        quality (:rarity cast-data)
        lifespan (config/get-cast-lifespan level false)
        expires-at (when lifespan (+ (now) lifespan))

        new-cast {:id (uuid)
                  :player-fid creator-fid
                  :is-npc? is-npc?
                  :text cast-text
                  :quality quality
                  :level level
                  :created-at (now)
                  :expires-at expires-at
                  :is-banger? false
                  :nft-minted? false}]

    (js/console.log (str "📝 Created " (name quality) " cast (L" level "): " (subs cast-text 0 30) "..."))

    (-> game-state
        (update :casts conj new-cast)
        (update-in [:player :stats :total-casts] (if is-npc? identity inc)))))

(defn player-cast [game-state]
  "Player generates a cast (requires level 5+)"
  (let [level (get-current-level game-state)
        features (config/get-level-features level)]

    (if-not (:can-cast? features)
      (do
        (js/console.log "🔒 Casting locked until level 5")
        game-state)

      (if-not (is-action-ready? game-state :cast)
        (do
          (js/console.log "⏳ Cast recharge not ready")
          game-state)

        (-> game-state
            (create-cast (get-in game-state [:player :fid]) false)
            (assoc-in [:recharge-meters :cast :last-action] (now))
            (assoc-in [:recharge-meters :cast :ready?] false)
            (add-xp (:cast-generated config/xp-sources) "cast generated"))))))

;; ============================================================================
;; LIKE SYSTEM
;; ============================================================================

(defn has-liked? [game-state cast-id player-fid]
  "Check if player has already liked this cast"
  (contains? (get-in game-state [:likes cast-id] #{}) player-fid))

(defn like-cast [game-state cast-id]
  "Player likes a cast"
  (let [player-fid (get-in game-state [:player :fid] "player")
        cast-index (.indexOf (map :id (:casts game-state)) cast-id)
        cast (when (>= cast-index 0) (nth (:casts game-state) cast-index))]

    (cond
      ;; Cast doesn't exist
      (< cast-index 0)
      (do
        (js/console.log "Cast not found")
        game-state)

      ;; Already liked
      (has-liked? game-state cast-id player-fid)
      (do
        (js/console.log "Already liked this cast")
        game-state)

      ;; Recharge not ready
      (not (is-action-ready? game-state :like))
      (do
        (js/console.log "⏳ Like recharge not ready")
        game-state)

      ;; Can like!
      :else
      (let [is-real-like (not (:is-npc? cast))
            likes (inc (count (get-in game-state [:likes cast-id] #{})))
            level (get-in cast [:level])
            features (config/get-level-features level)
            can-be-banger? (:can-create-bangers? features)
            becomes-banger? (and can-be-banger?
                                 (>= likes (:required-likes config/banger-threshold))
                                 (not (:is-banger? cast)))]

        (js/console.log (str "❤️ Liked cast (total: " likes ")"))

        (cond-> game-state
          ;; Record the like
          true (update-in [:likes cast-id] (fnil conj #{}) player-fid)

          ;; Update recharge meter
          true (assoc-in [:recharge-meters :like :last-action] (now))
          true (assoc-in [:recharge-meters :like :ready?] false)

          ;; Add XP for liking
          true (add-xp (:like-given config/xp-sources) "like given")

          ;; If cast owner gets XP (if not NPC)
          (not (:is-npc? cast))
          (add-xp (:like-received config/xp-sources) "like received")

          ;; Update stats
          true (update-in [:player :stats :total-likes-given] inc)

          ;; Check for banger
          becomes-banger?
          (-> (assoc-in [:casts cast-index :is-banger?] true)
              (assoc-in [:casts cast-index :expires-at] nil)
              (update-in [:player :stats :total-bangers] inc)
              (add-xp (:banger-achieved config/xp-sources) "banger achieved")))))))

;; ============================================================================
;; REPLY SYSTEM
;; ============================================================================

(def canned-replies
  ["this is great"
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
   "wagmi"])

(defn reply-to-cast [game-state cast-id reply-text]
  "Player replies to a cast"
  (let [cast-index (.indexOf (map :id (:casts game-state)) cast-id)
        cast (when (>= cast-index 0) (nth (:casts game-state) cast-index))]

    (cond
      ;; Cast doesn't exist
      (< cast-index 0)
      (do
        (js/console.log "Cast not found")
        game-state)

      ;; Recharge not ready
      (not (is-action-ready? game-state :reply))
      (do
        (js/console.log "⏳ Reply recharge not ready")
        game-state)

      ;; Can reply!
      :else
      (do
        (js/console.log (str "💬 Replied: " reply-text))

        (cond-> game-state
          ;; Add reply to cast (not implementing replies list yet for MVP)
          ;; true (update-in [:casts cast-index :replies] conj {:text reply-text :created-at (now)})

          ;; Update recharge meter
          true (assoc-in [:recharge-meters :reply :last-action] (now))
          true (assoc-in [:recharge-meters :reply :ready?] false)

          ;; Add XP for replying
          true (add-xp (:reply-given config/xp-sources) "reply given")

          ;; If cast owner gets XP (if not NPC)
          (not (:is-npc? cast))
          (add-xp (:reply-received config/xp-sources) "reply received")

          ;; Update stats
          true (update-in [:player :stats :total-replies-given] inc))))))

;; ============================================================================
;; FEED SYSTEM
;; ============================================================================

(defn get-active-casts [game-state]
  "Get all casts that haven't expired"
  (let [current-time (now)]
    (filter
     (fn [cast]
       (or (nil? (:expires-at cast))
           (> (:expires-at cast) current-time)))
     (:casts game-state))))

(defn get-visible-casts [game-state]
  "Get casts visible in feed (player's casts + NPC casts + other players within level range)"
  (let [player-level (get-current-level game-state)
        player-fid (get-in game-state [:player :fid])
        min-level (max 1 (- player-level config/feed-level-range))
        max-level (+ player-level config/feed-level-range)
        active-casts (get-active-casts game-state)]

    ;; For MVP: Show player's casts + NPC casts
    ;; Phase 2: Add real player casts from similar levels
    (take 20 (reverse active-casts))))

(defn refresh-feed [game-state]
  "Refresh the feed with current visible casts"
  (assoc-in game-state [:feed :visible-casts]
            (get-visible-casts game-state)))

;; ============================================================================
;; NPC SYSTEM
;; ============================================================================

(defn generate-npc-cast [game-state]
  "NPC generates a cast for the feed"
  (create-cast game-state (str "npc-" (rand-int 1000)) true))

(defn npc-tick [game-state]
  "NPC actors generate casts and interact"
  (let [player-level (get-current-level game-state)
        should-generate-cast? (< (rand) 0.3)]  ;; 30% chance to generate NPC cast

    (cond-> game-state
      ;; Generate NPC cast for early levels
      (and should-generate-cast? (< player-level 5))
      (generate-npc-cast))))

;; ============================================================================
;; GAME LOOP
;; ============================================================================

(defn tick [game-state]
  "Main game loop tick - called every second"
  (let [now (now)
        last-tick (get-in game-state [:game-loop :last-tick])
        last-npc-tick (get-in game-state [:game-loop :last-npc-tick])

        delta-ms (- now last-tick)
        delta-seconds (/ delta-ms 1000)

        ;; Check if it's time for NPC tick
        time-since-npc-tick (- now last-npc-tick)
        should-npc-tick? (>= time-since-npc-tick config/npc-tick-interval-ms)]

    (cond-> game-state
      ;; Update game time
      true (update-in [:player :stats :game-time] + delta-seconds)
      true (assoc-in [:game-loop :last-tick] now)

      ;; Update recharge meters
      true (update-recharge-meters)

      ;; NPC tick if needed
      should-npc-tick? (-> (npc-tick)
                           (assoc-in [:game-loop :last-npc-tick] now))

      ;; Refresh feed
      true (refresh-feed))))

;; ============================================================================
;; STATS & ANALYTICS
;; ============================================================================

(defn get-player-stats [game-state]
  "Get comprehensive player statistics"
  (let [player (:player game-state)
        casts (:casts game-state)
        player-casts (filter #(= (:player-fid %) (:fid player)) casts)
        quality-breakdown (frequencies (map :quality player-casts))
        bangers (filter :is-banger? player-casts)]
    {:level (:level player)
     :xp (:xp player)
     :xp-to-next (config/xp-to-next-level (:xp player))
     :xp-progress (get-xp-progress game-state)
     :stats (:stats player)
     :quality-breakdown quality-breakdown
     :bangers (count bangers)
     :active-casts (count (get-active-casts game-state))}))
