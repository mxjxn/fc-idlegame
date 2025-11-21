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
            :stats {:total-clicks 0
                    :total-casts 0
                    :total-likes-received 0
                    :total-bangers 0
                    :nfts-minted 0
                    :game-time 0}
            :memory {:short-term []
                     :long-term []
                     :topics []}}

   :casts []  ;; All casts ever created

   :feed {:visible-casts []  ;; Current feed items
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
;; CAST GENERATION SYSTEM
;; ============================================================================

(defn generate-cast-text [game-state quality]
  "Generate cast text based on level and quality
   MVP: All casts are canned
   Future: AI generation for levels 11+"
  (let [level (get-current-level game-state)
        features (config/get-level-features level)]

    ;; For MVP (levels 1-10), always use canned casts
    (if-not (:ai-generation? features)
      (:text (canned/get-random-cast))

      ;; Future: AI generation logic here
      ;; For now, still use canned
      (:text (canned/get-random-cast)))))

(defn create-cast [game-state]
  "Create a new cast"
  (let [quality (config/roll-quality)
        level (get-current-level game-state)
        player-fid (get-in game-state [:player :fid])
        cast-text (generate-cast-text game-state quality)
        lifespan (config/get-cast-lifespan level false)
        expires-at (when lifespan (+ (now) lifespan))

        new-cast {:id (uuid)
                  :player-fid player-fid
                  :text cast-text
                  :quality quality
                  :level level
                  :created-at (now)
                  :expires-at expires-at
                  :likes 0
                  :replies []
                  :is-banger? false
                  :nft-minted? false}]

    (js/console.log (str "📝 Created " (name quality) " cast: " (subs cast-text 0 30) "..."))

    (-> game-state
        (update :casts conj new-cast)
        (update-in [:player :stats :total-casts] inc)
        (add-xp (:idle-cast config/xp-sources) "idle cast"))))

;; ============================================================================
;; INTERACTION SYSTEM
;; ============================================================================

(defn click [game-state]
  "Handle manual click - generates a cast"
  (-> game-state
      (update-in [:player :stats :total-clicks] inc)
      (add-xp (:manual-click config/xp-sources) "manual click")
      (create-cast)))

(defn like-cast [game-state cast-id]
  "Add a like to a cast"
  (let [cast-index (.indexOf (map :id (:casts game-state)) cast-id)]
    (if (>= cast-index 0)
      (let [new-state (update-in game-state [:casts cast-index :likes] inc)
            likes (get-in new-state [:casts cast-index :likes])
            is-banger? (>= likes (:total-likes config/banger-threshold))]

        ;; Check if it became a banger
        (if (and is-banger?
                 (not (get-in game-state [:casts cast-index :is-banger?])))
          (do
            (js/console.log "🔥 BANGER ACHIEVED!")
            (-> new-state
                (assoc-in [:casts cast-index :is-banger?] true)
                (assoc-in [:casts cast-index :expires-at] nil)
                (update-in [:player :stats :total-bangers] inc)
                (add-xp (:banger-achieved config/xp-sources) "banger")))
          new-state))
      game-state)))

(defn add-reply [game-state cast-id reply-text]
  "Add a reply to a cast"
  (let [cast-index (.indexOf (map :id (:casts game-state)) cast-id)]
    (if (>= cast-index 0)
      (update-in game-state [:casts cast-index :replies]
                 conj {:text reply-text
                       :created-at (now)})
      game-state)))

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
  "Get casts visible in feed (within level range)"
  (let [player-level (get-current-level game-state)
        min-level (max 1 (- player-level config/feed-level-range))
        max-level (+ player-level config/feed-level-range)
        active-casts (get-active-casts game-state)]

    ;; For MVP, just show player's own casts
    ;; Future: Filter by level range and mix with other players
    (take 20 (reverse active-casts))))

(defn refresh-feed [game-state]
  "Refresh the feed with current visible casts"
  (assoc-in game-state [:feed :visible-casts]
            (get-visible-casts game-state)))

;; ============================================================================
;; NPC SYSTEM (Future: Phase 2)
;; ============================================================================

(defn npc-tick [game-state]
  "NPC actors randomly like casts"
  ;; For MVP, this is simplified
  ;; Future: More sophisticated NPC behavior
  (let [visible-casts (get-visible-casts game-state)
        should-like? (< (rand) config/npc-like-probability)]

    (if (and should-like? (seq visible-casts))
      (let [random-cast (rand-nth visible-casts)]
        (like-cast game-state (:id random-cast)))
      game-state)))

;; ============================================================================
;; GAME LOOP
;; ============================================================================

(defn tick [game-state]
  "Main game loop tick - called every second"
  (let [now (now)
        last-tick (get-in game-state [:game-loop :last-tick])
        last-idle-cast (get-in game-state [:game-loop :last-idle-cast])
        last-npc-tick (get-in game-state [:game-loop :last-npc-tick])

        delta-ms (- now last-tick)
        delta-seconds (/ delta-ms 1000)

        ;; Check if it's time for an idle cast
        time-since-idle-cast (- now last-idle-cast)
        should-idle-cast? (>= time-since-idle-cast config/idle-cast-interval-ms)

        ;; Check if it's time for NPC tick
        time-since-npc-tick (- now last-npc-tick)
        should-npc-tick? (>= time-since-npc-tick config/npc-tick-interval-ms)]

    (cond-> game-state
      ;; Update game time
      true (update-in [:player :stats :game-time] + delta-seconds)
      true (assoc-in [:game-loop :last-tick] now)

      ;; Generate idle cast if needed
      should-idle-cast? (-> (create-cast)
                            (assoc-in [:game-loop :last-idle-cast] now))

      ;; NPC tick if needed
      should-npc-tick? (-> (npc-tick)
                           (assoc-in [:game-loop :last-npc-tick] now))

      ;; Refresh feed
      true (refresh-feed))))

;; ============================================================================
;; SHARE SYSTEM (Future: Phase 2)
;; ============================================================================

(defn generate-share-data [game-state]
  "Generate data for share image"
  (let [player (:player game-state)
        recent-casts (take 3 (reverse (:casts game-state)))]
    {:level (:level player)
     :total-casts (get-in player [:stats :total-casts])
     :bangers (get-in player [:stats :total-bangers])
     :recent-casts (map :text recent-casts)
     :username (:username player)}))

;; ============================================================================
;; STATS & ANALYTICS
;; ============================================================================

(defn get-player-stats [game-state]
  "Get comprehensive player statistics"
  (let [player (:player game-state)
        casts (:casts game-state)
        quality-breakdown (frequencies (map :quality casts))
        bangers (filter :is-banger? casts)]
    {:level (:level player)
     :xp (:xp player)
     :xp-to-next (config/xp-to-next-level (:xp player))
     :xp-progress (get-xp-progress game-state)
     :stats (:stats player)
     :quality-breakdown quality-breakdown
     :bangers (count bangers)
     :active-casts (count (get-active-casts game-state))}))
