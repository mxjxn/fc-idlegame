(ns fc-idlegame.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [clojure.core.async :refer [go <!]]
            [fc-idlegame.game :as game]
            [fc-idlegame.views :as views]
            [fc-idlegame.firebase :as firebase]
            [fc-idlegame.config :as config]))

;; Notification helpers - keep only latest 5 notifications
(defn trim-old-notifications! [app-state]
  "Remove oldest notifications when there are more than 5"
  (swap! app-state update :notifications
         (fn [notifs]
           (if (> (count notifs) 5)
             (take-last 5 notifs)  ;; Keep only latest 5
             notifs))))

;; App State
(defonce app-state
  (r/atom {:initialized? false
           :farcaster {:username nil
                       :fid nil
                       :display-name nil}
           :game game/initial-game-state
           :notifications []  ;; Array of notification maps
           :stats-panel-open? false  ;; Toggle for stats panel
           :seen-cast-ids #{}}))  ;; Track which casts have been seen (for animations)

;; Farcaster SDK Integration
;; Uses @farcaster/miniapp-sdk npm package directly via JavaScript interop
(defn- get-farcaster-sdk []
  "Get Farcaster Mini App SDK - check window.farcaster first (mini-app context), then try require"
  (cond
    ;; In mini-app context, SDK is injected as window.farcaster
    (and (exists? js/window) (.-farcaster js/window))
    (do
      (js/console.log "📱 Found Farcaster SDK via window.farcaster")
      (.-farcaster js/window))
    ;; Fallback: try to require the npm package (for local dev)
    (exists? js/require)
    (try
      (let [sdk-module (js/require "@farcaster/miniapp-sdk")]
        (js/console.log "📦 Found Farcaster SDK via require")
        (or (.-default sdk-module) sdk-module))
      (catch js/Error e
        (js/console.warn "⚠️ Could not require Farcaster SDK:" (.-message e))
        nil))
    :else nil))

(defn init-farcaster! []
  (go
    (try
      (js/console.log "🚀 Initializing Farcaster SDK...")
      
      (let [sdk-instance (get-farcaster-sdk)]
        (if sdk-instance
          (do
            (js/console.log "✅ Farcaster SDK instance found, waiting for ready...")
            
            ;; Wait for SDK to be ready (returns a Promise)
            (let [ready-result (<! (js/Promise.resolve
                                    (if (and sdk-instance (.-ready sdk-instance))
                                      (.ready sdk-instance)
                                      (js/Promise.resolve true))))]
              (js/console.log "✅ Farcaster SDK ready, getting context...")
              
              ;; Get user context (returns a Promise)
              ;; The SDK's getContext() returns a Promise with { user: { fid, username, displayName, ... } }
              (let [context-result (<! (js/Promise.resolve
                                       (if (and sdk-instance (.-getContext sdk-instance))
                                         (.getContext sdk-instance)
                                         (if (and sdk-instance (.-context sdk-instance))
                                           (if (fn? (.-context sdk-instance))
                                             ((.-context sdk-instance))
                                             (.-context sdk-instance))
                                           (js/Promise.resolve nil)))))
                    context (if (map? context-result)
                             context-result
                             (try
                               (js->clj context-result :keywordize-keys true)
                               (catch js/Error e
                                 (js/console.warn "Could not convert context to map:" e)
                                 nil)))
                    user (or (:user context) (get context "user"))
                    username (or (:username user) (get user "username") (when user (.-username user)))
                    fid (or (:fid user) (get user "fid") (when user (.-fid user)))
                    display-name (or (:displayName user) (get user "displayName") (when user (.-displayName user)) (when user (.-display_name user)))]
                
                (js/console.log "📊 Context received:" context)
                (js/console.log "👤 User data:" user)
                (js/console.log "✅ Farcaster SDK initialized!" (str "User: " username " (FID: " fid ")"))
                
                (if (and username fid)
                  (do
                    ;; Update app state with real Farcaster data
                    (swap! app-state (fn [state]
                                      (-> state
                                          (assoc :initialized? true
                                                 :farcaster {:username username
                                                             :fid fid
                                                             :display-name display-name})
                                          (assoc-in [:game :player :fid] (str fid))
                                          (assoc-in [:game :player :username] username)
                                          (assoc-in [:game :player :display-name] display-name))))
                    
                    ;; Initialize Firebase after Farcaster is ready
                    (let [firebase-config (when-let [config-str (:firebase-config config/api-keys)]
                                            (try
                                              (js->clj (.parse js/JSON config-str) :keywordize-keys true)
                                              (catch js/Error e
                                                (js/console.warn "Failed to parse Firebase config:" e)
                                                nil)))
                          firebase-result (<! (firebase/init-firebase! firebase-config))]
                      (js/console.log "🔥 Firebase init result:" firebase-result)
                      
                      ;; Load player data from Firebase if available
                      (when (= (:mode firebase-result) :firebase)
                        (let [player-result (<! (firebase/load-player! (str fid)))]
                          (when (:success player-result)
                            (js/console.log "✅ Loaded player data from Firebase")
                            (swap! app-state assoc-in [:game :player] (:data player-result)))))))
                  (do
                    (js/console.warn "⚠️ Farcaster SDK available but no user data found, using mock data")
                    (swap! app-state (fn [state]
                                      (-> state
                                          (assoc :initialized? true
                                                 :farcaster {:username "player"
                                                             :fid 12345
                                                             :display-name "Player"})
                                          (assoc-in [:game :player :fid] "12345")
                                          (assoc-in [:game :player :username] "player")
                                          (assoc-in [:game :player :display-name] "Player")))))))))
          ;; Fallback if SDK not available
          (do
            (js/console.warn "⚠️ Farcaster SDK not available - using mock data for development")
            (swap! app-state (fn [state]
                              (-> state
                                  (assoc :initialized? true
                                         :farcaster {:username "player"
                                                     :fid 12345
                                                     :display-name "Player"})
                                  (assoc-in [:game :player :fid] "12345")
                                  (assoc-in [:game :player :username] "player")
                                  (assoc-in [:game :player :display-name] "Player")))))))
      
      (catch js/Error e
        (js/console.error "❌ Failed to initialize Farcaster SDK:" e)
        (js/console.error "❌ Error stack:" (.-stack e))
        ;; Fallback to mock data for development
        (js/console.warn "⚠️ Using mock data for development")
        (swap! app-state (fn [state]
                          (-> state
                              (assoc :initialized? true
                                     :farcaster {:username "player"
                                                 :fid 12345
                                                 :display-name "Player"})
                              (assoc-in [:game :player :fid] "12345")
                              (assoc-in [:game :player :username] "player")
                              (assoc-in [:game :player :display-name] "Player"))))))))

;; Game Loop
(defonce game-loop-interval (atom nil))

(defn start-game-loop! []
  (when-not @game-loop-interval
    (reset! game-loop-interval
            (js/setInterval
             (fn []
               (swap! app-state
                      (fn [state]
                        (let [new-game-state (game/tick (:game state))
                              pending-notifs (:pending-notifications new-game-state)
                              new-notifs (map (fn [n]
                                                (let [id (str "notif-" (.now js/Date) "-" (rand-int 1000000))]
                                                  {:id id
                                                   :type (:type n)
                                                   :username (:username n)
                                                   :message (:message n)}))
                                              pending-notifs)]
                          ;; Trim old notifications if more than 5
                          (let [updated-state (-> state
                                                  (assoc :game (dissoc new-game-state :pending-notifications))
                                                  (update :notifications concat new-notifs))]
                            (trim-old-notifications! app-state)
                            updated-state))))
             1000))))) ;; Tick every second

(defn stop-game-loop! []
  (when @game-loop-interval
    (js/clearInterval @game-loop-interval)
    (reset! game-loop-interval nil)))

;; App Root Component
(defn app []
  (let [state @app-state]
    (if (:initialized? state)
      [views/game-view state app-state]
      [views/loading-view])))

;; Lifecycle
(defn mount-root []
  (rdom/render [app]
               (.getElementById js/document "app")))

(defn init! []
  (js/console.log "🎮 Initializing Farcaster Idle Game...")
  (init-farcaster!)
  (start-game-loop!)
  (firebase/start-auto-save! app-state)
  (mount-root))

(defn reload! []
  (js/console.log "Reloading...")
  (mount-root))

;; Start the app
(defn ^:export main []
  (init!))
