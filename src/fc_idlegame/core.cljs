(ns fc-idlegame.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [clojure.core.async :refer [go <!]]
            [fc-idlegame.game :as game]
            [fc-idlegame.views :as views]))

;; App State
(defonce app-state
  (r/atom {:initialized? false
           :farcaster {:username nil
                       :fid nil
                       :display-name nil}
           :game game/initial-game-state}))

;; Farcaster SDK Integration
(defn init-farcaster! []
  (go
    (try
      ;; Initialize and ready the Farcaster SDK
      ;; NOTE: Replace with actual farcaster-cljs calls once library is added
      (js/console.log "Initializing Farcaster SDK...")

      ;; Placeholder for actual SDK initialization
      ;; (<! (fc/quick-start!))
      ;; (let [username (<! (fc/get-username))
      ;;       fid (<! (fc/get-fid))
      ;;       display-name (<! (fc/get-display-name))]
      ;;   (swap! app-state assoc
      ;;          :initialized? true
      ;;          :farcaster {:username username
      ;;                      :fid fid
      ;;                      :display-name display-name}))

      ;; Mock data for now
      (swap! app-state assoc
             :initialized? true
             :farcaster {:username "player"
                         :fid 12345
                         :display-name "Player"})

      (js/console.log "Farcaster SDK initialized!")
      (catch js/Error e
        (js/console.error "Failed to initialize Farcaster SDK:" e)))))

;; Game Loop
(defonce game-loop-interval (atom nil))

(defn start-game-loop! []
  (when-not @game-loop-interval
    (reset! game-loop-interval
            (js/setInterval
             #(swap! app-state update :game game/tick)
             1000)))) ;; Tick every second

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
  (js/console.log "Initializing app...")
  (init-farcaster!)
  (start-game-loop!)
  (mount-root))

(defn reload! []
  (js/console.log "Reloading...")
  (mount-root))

;; Start the app
(defn ^:export main []
  (init!))
