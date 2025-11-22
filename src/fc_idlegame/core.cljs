(ns fc-idlegame.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [clojure.core.async :refer [go <!]]
            [fc-idlegame.game :as game]
            [fc-idlegame.views :as views]))

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
      (swap! app-state (fn [state]
                         (-> state
                             (assoc :initialized? true
                                    :farcaster {:username "player"
                                                :fid 12345
                                                :display-name "Player"})
                             (assoc-in [:game :player :fid] 12345)
                             (assoc-in [:game :player :username] "player")
                             (assoc-in [:game :player :display-name] "Player"))))

      (js/console.log "Farcaster SDK initialized!")
      (catch js/Error e
        (js/console.error "Failed to initialize Farcaster SDK:" e)))))

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
