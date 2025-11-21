(ns fc-idlegame.firebase
  (:require [clojure.core.async :refer [go chan put! <!]]
            [fc-idlegame.config :as config]))

;; ============================================================================
;; FIREBASE INTEGRATION
;; Phase 1 (MVP): Stub implementation with localStorage
;; Phase 2: Full Firebase Firestore integration
;; ============================================================================

;; Firebase will be initialized with config like:
;; {
;;   :apiKey "..."
;;   :authDomain "..."
;;   :projectId "..."
;;   :storageBucket "..."
;;   :messagingSenderId "..."
;;   :appId "..."
;; }

(defonce firebase-app (atom nil))
(defonce firestore-db (atom nil))
(defonce initialized? (atom false))

;; ============================================================================
;; INITIALIZATION
;; ============================================================================

(defn init-firebase! [firebase-config]
  "Initialize Firebase (stub for MVP)"
  (go
    (try
      (js/console.log "Firebase init (stub) - will use localStorage for MVP")
      ;; Future: Actual Firebase initialization
      ;; (let [firebase (js/require "firebase/app")
      ;;       firestore (js/require "firebase/firestore")
      ;;       app (.initializeApp firebase (clj->js firebase-config))
      ;;       db (.getFirestore firestore app)]
      ;;   (reset! firebase-app app)
      ;;   (reset! firestore-db db)
      ;;   (reset! initialized? true))

      (reset! initialized? true)
      {:success true}
      (catch js/Error e
        (js/console.error "Firebase init error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; PLAYER DATA
;; ============================================================================

(defn save-player! [player-data]
  "Save player data to Firebase (stub: uses localStorage)"
  (go
    (try
      (let [player-json (.stringify js/JSON (clj->js player-data))]
        (.setItem js/localStorage "player-data" player-json)
        (js/console.log "Player data saved to localStorage")
        {:success true})
      (catch js/Error e
        (js/console.error "Save error:" e)
        {:success false :error (.-message e)}))))

(defn load-player! [fid]
  "Load player data from Firebase (stub: uses localStorage)"
  (go
    (try
      (let [player-json (.getItem js/localStorage "player-data")]
        (if player-json
          (let [player-data (js->clj (.parse js/JSON player-json) :keywordize-keys true)]
            (js/console.log "Player data loaded from localStorage")
            {:success true :data player-data})
          {:success false :error "No saved data found"}))
      (catch js/Error e
        (js/console.error "Load error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; CAST DATA
;; ============================================================================

(defn save-cast! [cast-data]
  "Save a cast to Firebase (stub for MVP)"
  (go
    (try
      ;; For MVP, casts are stored in memory only
      ;; Future: Save to Firestore
      (js/console.log "Cast saved (memory only)" (:id cast-data))
      {:success true :cast-id (:id cast-data)}
      (catch js/Error e
        {:success false :error (.-message e)}))))

(defn load-feed! [player-level]
  "Load feed casts from Firebase (stub for MVP)"
  (go
    (try
      ;; For MVP, return empty feed (only show own casts)
      ;; Future: Query Firestore for casts from players ±2 levels
      {:success true :casts []}
      (catch js/Error e
        {:success false :error (.-message e)}))))

(defn like-cast! [cast-id player-fid]
  "Record a like in Firebase (stub for MVP)"
  (go
    (try
      ;; Future: Save like to Firestore and check for banger status
      (js/console.log "Like recorded" cast-id)
      {:success true}
      (catch js/Error e
        {:success false :error (.-message e)}))))

;; ============================================================================
;; BANGER DATA
;; ============================================================================

(defn save-banger! [cast-data]
  "Save a banger to Firebase (stub for MVP)"
  (go
    (try
      (js/console.log "🔥 Banger saved" (:id cast-data))
      ;; Future: Save to special 'bangers' collection
      {:success true}
      (catch js/Error e
        {:success false :error (.-message e)}))))

(defn load-bangers! [player-fid]
  "Load player's bangers from Firebase"
  (go
    (try
      ;; Future: Query bangers collection
      {:success true :bangers []}
      (catch js/Error e
        {:success false :error (.-message e)}))))

;; ============================================================================
;; GAME STATE PERSISTENCE
;; ============================================================================

(defn save-game-state! [game-state]
  "Save entire game state to localStorage (MVP) or Firebase (future)"
  (go
    (try
      (let [state-json (.stringify js/JSON (clj->js game-state))]
        (.setItem js/localStorage "game-state" state-json)
        (js/console.log "Game state saved")
        {:success true})
      (catch js/Error e
        (js/console.error "Save game state error:" e)
        {:success false :error (.-message e)}))))

(defn load-game-state! []
  "Load game state from localStorage (MVP) or Firebase (future)"
  (go
    (try
      (let [state-json (.getItem js/localStorage "game-state")]
        (if state-json
          (let [game-state (js->clj (.parse js/JSON state-json) :keywordize-keys true)]
            (js/console.log "Game state loaded")
            {:success true :data game-state})
          {:success false :error "No saved game state"}))
      (catch js/Error e
        (js/console.error "Load game state error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; AUTO-SAVE SYSTEM
;; ============================================================================

(defonce auto-save-interval (atom nil))

(defn start-auto-save! [app-state]
  "Start auto-saving game state every 30 seconds"
  (when-not @auto-save-interval
    (reset! auto-save-interval
            (js/setInterval
             #(go
                (let [game-state (:game @app-state)]
                  (<! (save-game-state! game-state))))
             30000)))) ;; Save every 30 seconds

(defn stop-auto-save! []
  "Stop auto-saving"
  (when @auto-save-interval
    (js/clearInterval @auto-save-interval)
    (reset! auto-save-interval nil)))

;; ============================================================================
;; FUTURE: OPENAI INTEGRATION
;; ============================================================================

(defn generate-ai-cast! [prompt context]
  "Generate a cast using OpenAI (Phase 3)"
  (go
    (try
      ;; Future: Call OpenAI API
      ;; (let [response (openai/chat-completion
      ;;                 {:model "gpt-4"
      ;;                  :messages [{:role "system" :content "You are a creative Farcaster user..."}
      ;;                             {:role "user" :content prompt}]
      ;;                  :max_tokens 100})]
      ;;   {:success true :text (:content response)})

      ;; For now, return placeholder
      {:success false :error "AI generation not yet implemented"}
      (catch js/Error e
        {:success false :error (.-message e)}))))

;; ============================================================================
;; FUTURE: FARCASTER API INTEGRATION
;; ============================================================================

(defn fetch-user-casts! [fid limit]
  "Fetch user's recent casts from Farcaster API (Phase 3)"
  (go
    (try
      ;; Future: Call Farcaster API
      {:success false :error "Farcaster API integration not yet implemented"}
      (catch js/Error e
        {:success false :error (.-message e)}))))
