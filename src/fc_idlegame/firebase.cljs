(ns fc-idlegame.firebase
  (:require [clojure.core.async :refer [go chan put! <!]]
            [fc-idlegame.config :as config]))

;; ============================================================================
;; FIREBASE INTEGRATION
;; Full Firebase Firestore integration (Phase 2)
;; ============================================================================

;; Firebase config structure:
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
(defonce firebase-mode (atom :localStorage)) ;; :firebase or :localStorage

;; ============================================================================
;; FIREBASE MODULE HELPERS
;; ============================================================================

(defn- use-firebase? []
  "Check if Firebase is available via global or require"
  (or (exists? js/firebase)
      (and (exists? js/require)
           (try
             (js/require "firebase/app")
             true
             (catch js/Error e false)))))

(defn- get-firebase-app-module []
  "Get Firebase app module"
  (cond
    (exists? js/firebase) js/firebase
    (exists? js/require) (try (js/require "firebase/app") (catch js/Error e nil))
    :else nil))

(defn- get-firestore-module []
  "Get Firestore module"
  (cond
    (and (exists? js/firebase) (.-firestore js/firebase)) (.-firestore js/firebase)
    (exists? js/require) (try (js/require "firebase/firestore") (catch js/Error e nil))
    :else nil))

;; ============================================================================
;; INITIALIZATION
;; ============================================================================

(defn init-firebase! [firebase-config]
  "Initialize Firebase App and Firestore
   firebase-config: map with :apiKey, :authDomain, :projectId, etc.
   Returns channel with {:success bool, :mode :firebase|:localStorage}"
  (go
    (try
      (if (or (nil? firebase-config) (empty? firebase-config))
        (do
          (js/console.warn "⚠️ Firebase config not provided - using localStorage fallback")
          (reset! initialized? true)
          (reset! firebase-mode :localStorage)
          {:success true :mode :localStorage})
        (let [firebase-app-module (get-firebase-app-module)
              firestore-module (get-firestore-module)]
          (if (and firebase-app-module firestore-module)
            (try
              ;; Firebase v9+ modular SDK
              (let [initializeApp (.-initializeApp firebase-app-module)
                    getFirestore (.-getFirestore firestore-module)
                    app (initializeApp (clj->js firebase-config))
                    db (getFirestore app)]
                (reset! firebase-app app)
                (reset! firestore-db db)
                (reset! initialized? true)
                (reset! firebase-mode :firebase)
                (js/console.log "✅ Firebase initialized successfully")
                {:success true :mode :firebase})
              (catch js/Error e
                (js/console.error "❌ Firebase initialization error:" e)
                (reset! initialized? true)
                (reset! firebase-mode :localStorage)
                {:success false :error (.-message e) :mode :localStorage}))
            (do
              (js/console.warn "⚠️ Firebase modules not available - using localStorage fallback")
              (reset! initialized? true)
              (reset! firebase-mode :localStorage)
              {:success true :mode :localStorage}))))
      (catch js/Error e
        (js/console.error "❌ Firebase init error:" e)
        (reset! initialized? true)
        (reset! firebase-mode :localStorage)
        {:success false :error (.-message e) :mode :localStorage}))))

;; ============================================================================
;; FIRESTORE HELPERS
;; ============================================================================

(defn- get-firestore-functions []
  "Get Firestore functions from module"
  (let [firestore-module (get-firestore-module)]
    (when firestore-module
      {:collection (.-collection firestore-module)
       :doc (.-doc firestore-module)
       :setDoc (.-setDoc firestore-module)
       :getDoc (.-getDoc firestore-module)
       :addDoc (.-addDoc firestore-module)
       :updateDoc (.-updateDoc firestore-module)
       :deleteDoc (.-deleteDoc firestore-module)
       :query (.-query firestore-module)
       :where (.-where firestore-module)
       :orderBy (.-orderBy firestore-module)
       :limit (.-limit firestore-module)
       :onSnapshot (.-onSnapshot firestore-module)
       :serverTimestamp (.-serverTimestamp firestore-module)})))

(defn- collection-ref [collection-name]
  "Get Firestore collection reference"
  (when (= @firebase-mode :firebase)
    (let [fs-fns (get-firestore-functions)
          db @firestore-db]
      (when (and fs-fns db)
        ((:collection fs-fns) db collection-name)))))

(defn- doc-ref [collection-name doc-id]
  "Get Firestore document reference"
  (when (= @firebase-mode :firebase)
    (let [fs-fns (get-firestore-functions)
          db @firestore-db]
      (when (and fs-fns db)
        ((:doc fs-fns) ((:collection fs-fns) db collection-name) doc-id)))))

;; ============================================================================
;; PLAYER DATA
;; ============================================================================

(defn save-player! [player-data]
  "Save player data to Firebase or localStorage"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              fid (get-in player-data [:fid])
              player-ref (doc-ref (:players config/firebase-collections) (str fid))]
          (if (and fs-fns player-ref)
            (do
              (<! (js/Promise.resolve
                    ((:setDoc fs-fns) player-ref (clj->js player-data))))
              (js/console.log "✅ Player data saved to Firestore")
              {:success true})
            {:success false :error "Firestore not available"}))
        ;; localStorage fallback
        (let [player-json (.stringify js/JSON (clj->js player-data))]
          (.setItem js/localStorage "player-data" player-json)
          (js/console.log "💾 Player data saved to localStorage")
          {:success true}))
      (catch js/Error e
        (js/console.error "❌ Save error:" e)
        {:success false :error (.-message e)}))))

(defn load-player! [fid]
  "Load player data from Firebase or localStorage"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              player-ref (doc-ref (:players config/firebase-collections) (str fid))]
          (if (and fs-fns player-ref)
            (let [doc-snapshot (<! (js/Promise.resolve ((:getDoc fs-fns) player-ref)))]
              (if (.-exists doc-snapshot)
                (let [player-data (js->clj (.-data doc-snapshot) :keywordize-keys true)]
                  (js/console.log "✅ Player data loaded from Firestore")
                  {:success true :data player-data})
                {:success false :error "Player not found"}))
            {:success false :error "Firestore not available"}))
        ;; localStorage fallback
        (let [player-json (.getItem js/localStorage "player-data")]
          (if player-json
            (let [player-data (js->clj (.parse js/JSON player-json) :keywordize-keys true)]
              (js/console.log "💾 Player data loaded from localStorage")
              {:success true :data player-data})
            {:success false :error "No saved data found"})))
      (catch js/Error e
        (js/console.error "❌ Load error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; CAST DATA
;; ============================================================================

(defn save-cast! [cast-data]
  "Save a cast to Firebase or memory"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              casts-ref (collection-ref (:casts config/firebase-collections))]
          (if (and fs-fns casts-ref)
            (let [cast-with-timestamp (assoc cast-data
                                             :created-at (if-let [server-ts-fn (:serverTimestamp fs-fns)]
                                                           (server-ts-fn)
                                                           (.now js/Date)))]
              (<! (js/Promise.resolve
                    ((:addDoc fs-fns) casts-ref (clj->js cast-with-timestamp))))
              (js/console.log "✅ Cast saved to Firestore" (:id cast-data))
              {:success true :cast-id (:id cast-data)})
            {:success false :error "Firestore not available"}))
        ;; Memory only for MVP
        (do
          (js/console.log "💾 Cast saved (memory only)" (:id cast-data))
          {:success true :cast-id (:id cast-data)}))
      (catch js/Error e
        (js/console.error "❌ Save cast error:" e)
        {:success false :error (.-message e)}))))

(defn load-feed! [player-level]
  "Load feed casts from Firebase (players within ±2 levels)"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              casts-ref (collection-ref (:casts config/firebase-collections))
              min-level (max 1 (- player-level 2))
              max-level (+ player-level 2)]
          (if (and fs-fns casts-ref)
            (let [q1 ((:where fs-fns) ((:query fs-fns) casts-ref) "level" ">=" min-level)
                  q2 ((:where fs-fns) q1 "level" "<=" max-level)
                  q3 ((:orderBy fs-fns) q2 "created-at" "desc")
                  q4 ((:limit fs-fns) q3 50)
                  query-snapshot (<! (js/Promise.resolve ((:getDoc fs-fns) q4)))]
              (let [casts (map (fn [doc]
                                  (js->clj (.-data doc) :keywordize-keys true))
                                (.-docs query-snapshot))]
                (js/console.log "✅ Feed loaded from Firestore" (count casts) "casts")
                {:success true :casts casts}))
            {:success false :error "Firestore not available" :casts []}))
        ;; Empty feed for MVP (only show own casts)
        {:success true :casts []})
      (catch js/Error e
        (js/console.error "❌ Load feed error:" e)
        {:success false :error (.-message e) :casts []}))))

(defn like-cast! [cast-id player-fid]
  "Record a like in Firebase"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              like-ref (doc-ref (:likes config/firebase-collections)
                                (str cast-id "-" player-fid))
              like-data {:cast-id cast-id
                         :player-fid player-fid
                         :created-at (if-let [server-ts-fn (:serverTimestamp fs-fns)]
                                       (server-ts-fn)
                                       (.now js/Date))}]
          (if (and fs-fns like-ref)
            (do
              (<! (js/Promise.resolve
                    ((:setDoc fs-fns) like-ref (clj->js like-data))))
              ;; Update cast like count
              (let [cast-ref (doc-ref (:casts config/firebase-collections) cast-id)]
                (when cast-ref
                  (<! (js/Promise.resolve
                        ((:updateDoc fs-fns) cast-ref
                         (clj->js {:likes (inc (or (get-in like-data [:likes]) 0))}))))))
              (js/console.log "✅ Like recorded" cast-id)
              {:success true})
            {:success false :error "Firestore not available"}))
        ;; Stub for MVP
        (do
          (js/console.log "💾 Like recorded (memory only)" cast-id)
          {:success true}))
      (catch js/Error e
        (js/console.error "❌ Like error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; REPLY DATA
;; ============================================================================

(defn save-reply! [cast-id reply-data]
  "Save a reply to a cast"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              replies-ref (collection-ref (:replies config/firebase-collections))
              reply-with-meta (assoc reply-data
                                     :cast-id cast-id
                                     :created-at (if-let [server-ts-fn (:serverTimestamp fs-fns)]
                                                   (server-ts-fn)
                                                   (.now js/Date)))]
          (if (and fs-fns replies-ref)
            (do
              (<! (js/Promise.resolve
                    ((:addDoc fs-fns) replies-ref (clj->js reply-with-meta))))
              (js/console.log "✅ Reply saved to Firestore")
              {:success true})
            {:success false :error "Firestore not available"}))
        ;; Stub for MVP
        {:success true})
      (catch js/Error e
        (js/console.error "❌ Save reply error:" e)
        {:success false :error (.-message e)}))))

;; ============================================================================
;; BANGER DATA
;; ============================================================================

(defn save-banger! [cast-data]
  "Save a banger to Firebase"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              banger-ref (doc-ref (:bangers config/firebase-collections) (:id cast-data))]
          (if (and fs-fns banger-ref)
            (do
              (<! (js/Promise.resolve
                    ((:setDoc fs-fns) banger-ref (clj->js cast-data))))
              (js/console.log "🔥 Banger saved to Firestore" (:id cast-data))
              {:success true})
            {:success false :error "Firestore not available"}))
        ;; Stub for MVP
        (do
          (js/console.log "🔥 Banger saved (memory only)" (:id cast-data))
          {:success true}))
      (catch js/Error e
        (js/console.error "❌ Save banger error:" e)
        {:success false :error (.-message e)}))))

(defn load-bangers! [player-fid]
  "Load player's bangers from Firebase"
  (go
    (try
      (if (= @firebase-mode :firebase)
        (let [fs-fns (get-firestore-functions)
              bangers-ref (collection-ref (:bangers config/firebase-collections))
              q1 ((:where fs-fns) ((:query fs-fns) bangers-ref) "player-fid" "==" player-fid)
              q2 ((:orderBy fs-fns) q1 "created-at" "desc")
              query-snapshot (<! (js/Promise.resolve ((:getDoc fs-fns) q2)))]
          (let [bangers (map (fn [doc]
                               (js->clj (.-data doc) :keywordize-keys true))
                             (.-docs query-snapshot))]
            (js/console.log "✅ Bangers loaded from Firestore" (count bangers))
            {:success true :bangers bangers}))
        ;; Empty for MVP
        {:success true :bangers []})
      (catch js/Error e
        (js/console.error "❌ Load bangers error:" e)
        {:success false :error (.-message e) :bangers []}))))

;; ============================================================================
;; GAME STATE PERSISTENCE
;; ============================================================================

(defn save-game-state! [game-state]
  "Save entire game state to localStorage (MVP) or Firebase (future)"
  (go
    (try
      (let [state-json (.stringify js/JSON (clj->js game-state))]
        (.setItem js/localStorage "game-state" state-json)
        (js/console.log "💾 Game state saved")
        {:success true})
      (catch js/Error e
        (js/console.error "❌ Save game state error:" e)
        {:success false :error (.-message e)}))))

(defn load-game-state! []
  "Load game state from localStorage (MVP) or Firebase (future)"
  (go
    (try
      (let [state-json (.getItem js/localStorage "game-state")]
        (if state-json
          (let [game-state (js->clj (.parse js/JSON state-json) :keywordize-keys true)]
            (js/console.log "💾 Game state loaded")
            {:success true :data game-state})
          {:success false :error "No saved game state"}))
      (catch js/Error e
        (js/console.error "❌ Load game state error:" e)
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
;; REAL-TIME LISTENERS
;; ============================================================================

(defn subscribe-to-feed! [player-level callback]
  "Subscribe to real-time feed updates"
  (when (= @firebase-mode :firebase)
    (let [fs-fns (get-firestore-functions)
          casts-ref (collection-ref (:casts config/firebase-collections))
          min-level (max 1 (- player-level 2))
          max-level (+ player-level 2)]
      (when (and fs-fns casts-ref)
        (let [q1 ((:where fs-fns) ((:query fs-fns) casts-ref) "level" ">=" min-level)
              q2 ((:where fs-fns) q1 "level" "<=" max-level)
              q3 ((:orderBy fs-fns) q2 "created-at" "desc")
              q4 ((:limit fs-fns) q3 50)]
          ((:onSnapshot fs-fns) q4
           (fn [snapshot]
             (let [casts (map (fn [doc]
                                 (js->clj (.-data doc) :keywordize-keys true))
                               (.-docs snapshot))]
               (callback casts)))))))))

;; ============================================================================
;; FUTURE: OPENAI INTEGRATION
;; ============================================================================

(defn generate-ai-cast! [prompt context]
  "Generate a cast using OpenAI (Phase 3)"
  (go
    (try
      ;; Future: Call OpenAI API
      {:success false :error "AI generation not yet implemented"}
      (catch js/Error e
        {:success false :error (.-message e)}))))

;; ============================================================================
;; FUTURE: FARCASTER API INTEGRATION
;; ============================================================================

(defn fetch-user-casts! [fid limit]
  "Fetch user's recent casts from Farcaster API (Phase 3)
   Uses farcaster-cljs library to fetch casts"
  (go
    (try
      ;; Use farcaster-cljs to fetch user casts
      ;; Note: This will be implemented when farcaster-cljs API is available
      {:success false :error "Farcaster API integration - awaiting farcaster-cljs API methods"}
      (catch js/Error e
        {:success false :error (.-message e)}))))
