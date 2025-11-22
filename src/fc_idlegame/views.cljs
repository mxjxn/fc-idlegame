(ns fc-idlegame.views
  (:require [clojure.set :as set]
            [reagent.core :as r]
            [fc-idlegame.game :as game]
            [fc-idlegame.config :as config]))

;; ============================================================================
;; AVATAR SYSTEM (Simple Gravatar-like)
;; ============================================================================

(def emoji-palette
  ["😀" "😃" "😄" "😁" "😆" "😅" "🤣" "😂" "🙂" "🙃" "😉" "😊" "😇" "🥰" "😍" "🤩"
   "😘" "😗" "😚" "😙" "😋" "😛" "😜" "🤪" "😝" "🤑" "🤗" "🤭" "🤫" "🤔" "🤐" "🤨"
   "😐" "😑" "😶" "😏" "😒" "🙄" "😬" "🤥" "😌" "😔" "😪" "🤤" "😴" "😷" "🤒" "🤕"
   "🤢" "🤮" "🤧" "🥵" "🥶" "😶‍🌫️" "😵" "🤯" "🤠" "🥳" "😎" "🤓" "🧐" "😕" "😟" "🙁"
   "☹️" "😮" "😯" "😲" "😳" "🥺" "😦" "😧" "😨" "😰" "😥" "😢" "😭" "😱" "😖" "😣"])

(def color-palette
  ["#FF6B6B" "#4ECDC4" "#45B7D1" "#FFA07A" "#98D8C8" "#F7DC6F" "#BB8FCE" "#85C1E2"
   "#F8B739" "#52BE80" "#EC7063" "#5DADE2" "#F39C12" "#1ABC9C" "#E74C3C" "#3498DB"])

(defn simple-hash [s]
  "Simple hash function for consistent ID generation"
  (let [str-val (str s)
        hash (reduce (fn [acc idx]
                       (+ acc (* 31 (.charCodeAt str-val idx))))
                     0
                     (range (count str-val)))]
    (Math/abs hash)))

(defn get-avatar [fid]
  "Generate a consistent avatar (emoji + color) for a given fid"
  (let [hash (simple-hash fid)
        emoji-index (mod hash (count emoji-palette))
        color-index (mod (quot hash (count emoji-palette)) (count color-palette))
        emoji (nth emoji-palette emoji-index)
        color (nth color-palette color-index)
        ;; Generate a short identifier (first 4 chars of hash)
        identifier (subs (str hash) 0 (min 4 (count (str hash))))]
    {:emoji emoji
     :color color
     :identifier identifier}))

;; ============================================================================
;; LOADING VIEW
;; ============================================================================

(defn loading-view []
  [:div.loading
   [:div.spinner "⏳"]
   [:h2 "Initializing Farcaster Idle Game..."]])

;; ============================================================================
;; XP PROGRESS BAR
;; ============================================================================

(defn xp-progress-bar [game]
  (let [player (:player game)
        progress (game/get-xp-progress game)
        xp-to-next (config/xp-to-next-level (:xp player))]
    [:div.xp-container
     [:div.xp-info
      [:span "Level " (:level player)]
      [:span.xp-separator " • "]
      [:span.xp-remaining (game/format-number xp-to-next) " XP to next level"]]
     [:div.xp-bar-container
      [:div.xp-bar {:style {:width (str progress "%")}}]]]))

;; ============================================================================
;; CAST GENERATOR (CLICK BUTTON)
;; ============================================================================

(defn cooldown-ring [progress _label _time-remaining ring-type]
  "Render a cooldown ring indicator
   ring-type: :inner, :middle, or :outer"
  (let [inner-radius 12   ;; Smallest ring (pie slice)
        middle-radius 24  ;; Middle ring
        outer-radius 36   ;; Outer ring
        radius (case ring-type
                 :inner inner-radius
                 :middle middle-radius
                 :outer outer-radius)
        circumference (* 2 js/Math.PI radius)
        offset (* circumference (- 1 progress))]
    [:div.cooldown-ring-container {:class (str "ring-" (name ring-type))}
     [:svg.cooldown-ring {:viewBox "0 0 100 100"}
      [:circle.ring-bg {:cx 50 :cy 50 :r radius :fill "none"}]
      [:circle.ring-progress
       {:cx 50
        :cy 50
        :r radius
        :fill "none"
        :stroke-dasharray circumference
        :stroke-dashoffset offset
        :transform "rotate(-90 50 50)"
        :style {:transition "stroke-dashoffset 0.3s"}}]]]))

(defn cooldown-labels [manual-time idle-time reply-time]
  "Display cooldown labels next to the rings"
  [:div.cooldown-labels
   [:div.cooldown-label-row
    [:span.cooldown-label-name "click:"]
    [:span.cooldown-label-time {:class (when (<= manual-time 0) "ready")}
     (if (<= manual-time 0) "ready!" (game/format-recharge-time manual-time))]]
   [:div.cooldown-label-row
    [:span.cooldown-label-name "idle:"]
    [:span.cooldown-label-time {:class (when (<= idle-time 0) "ready")}
     (if (<= idle-time 0) "ready!" (game/format-recharge-time idle-time))]]
   [:div.cooldown-label-row
    [:span.cooldown-label-name "reply:"]
    [:span.cooldown-label-time {:class (when (<= reply-time 0) "ready")}
     (if (<= reply-time 0) "ready!" (game/format-recharge-time reply-time))]]])

;; ============================================================================
;; HEADER COMPONENT
;; ============================================================================

(defn header [{:keys [farcaster game]} app-state]
  (let [player (:player game)
        level (:level player)
        features (config/get-level-features level)
        can-cast? (:can-cast? features)
        
        ;; Manual cast cooldown
        manual-ready? (game/is-action-ready? game :cast)
        manual-progress (game/get-recharge-progress game :cast)
        manual-time-remaining (game/get-time-until-ready game :cast)
        
        ;; Idle cast cooldown
        idle-ready? (game/is-action-ready? game :idle-cast)
        idle-progress (game/get-recharge-progress game :idle-cast)
        idle-time-remaining (game/get-time-until-ready game :idle-cast)
        
        ;; Reply cooldown
        reply-ready? (game/is-action-ready? game :reply)
        reply-progress (game/get-recharge-progress game :reply)
        reply-time-remaining (game/get-time-until-ready game :reply)]
    [:header.game-header
     ;; Line 1: "idlecast" and player info
     [:div.header-top
      [:h1 "idlecast"]
      [:div.player-info
       [:span.username (:display-name farcaster)]
       (when (:fid farcaster)
         [:span.fid " (FID: " (:fid farcaster) ")"])]
      [:button.stats-toggle-button
       {:on-click #(swap! app-state update :stats-panel-open? not)
        :title "View Stats & Features"}
       "📊"]]
     ;; Line 2: Stats, XP progress, cooldown rings, cast button (all inline)
     [:div.header-bottom
      [:div.header-progress-row
       [xp-progress-bar game]
       [:div.cooldown-indicator
        [:div.cooldown-rings
         [cooldown-ring manual-progress "Manual" manual-time-remaining :outer]
         [cooldown-ring idle-progress "Idle" idle-time-remaining :middle]
         [cooldown-ring reply-progress "Reply" reply-time-remaining :inner]]
        [cooldown-labels manual-time-remaining idle-time-remaining reply-time-remaining]]]
      [:button.cast-button
       {:on-click #(swap! app-state update :game game/player-cast)
        :class (when-not (and can-cast? manual-ready?) "disabled")
        :disabled (not (and can-cast? manual-ready?))}
       (cond
         (not can-cast?) "🔒 Casting Locked"
         (not manual-ready?) (str "⏳ Ready in " (game/format-recharge-time manual-time-remaining))
         :else "🎯 Click to Cast!")]]]))

;; ============================================================================
;; CAST CARD COMPONENT
;; ============================================================================

(defn cast-card [cast app-state]
  (let [game (:game @app-state)
        player-fid (get-in game [:player :fid])
        cast-fid (:player-fid cast)
        is-own-cast? (= cast-fid player-fid)
        is-npc? (:is-npc? cast)
        quality-tier (get config/quality-tiers (:quality cast))
        age-seconds (/ (- (game/now) (:created-at cast)) 1000)
        age-display (cond
                      (< age-seconds 60) "just now"
                      (< age-seconds 3600) (str (Math/floor (/ age-seconds 60)) "m ago")
                      (< age-seconds 86400) (str (Math/floor (/ age-seconds 3600)) "h ago")
                      :else (str (Math/floor (/ age-seconds 86400)) "d ago"))
        avatar (get-avatar cast-fid)]
    [:div.cast-card
     {:class (str "quality-" (name (:quality cast))
                  (when (:is-banger? cast) " banger")
                  (when is-own-cast? " own-cast"))}

     [:div.cast-header
      [:div.cast-author
       [:div.author-avatar
        {:style {:background-color (:color avatar)}}
        (:emoji avatar)]
       [:div.author-info
        (if is-own-cast?
          [:span.author-name "You"]
          [:span.author-name
           (if is-npc?
             (str "anon-" (:identifier avatar))
             (str "user-" (:identifier avatar)))])]]
      
      [:div.cast-quality-badge
       [:span.quality-emoji (:emoji quality-tier)]
       [:span.quality-name (:name quality-tier)]]
      
      [:div.cast-meta
       [:span.cast-level "L" (:level cast)]
       [:span.cast-age age-display]]
      
      (when (:is-banger? cast)
        [:div.banger-badge "🔥 BANGER"])]

     [:div.cast-body
      [:p.cast-text (:text cast)]]

     [:div.cast-footer
      (let [has-liked? (game/has-liked? game (:id cast) player-fid)]
        [:button.like-button
         {:on-click #(swap! app-state update :game game/like-cast (:id cast))
          :class (when has-liked? "liked")}
         "❤️ " (count (get-in game [:likes (:id cast)] #{}))])
      
      [:button.reply-button
       {:on-click #(swap! app-state update :game game/reply-to-cast (:id cast))
        :disabled (not (game/is-action-ready? game :reply))}
       "💬 " (count (:replies cast []))]

      (when (:is-banger? cast)
        [:button.share-button "📤 Share"])

      (when (and (:is-banger? cast) (not (:nft-minted? cast)))
        [:button.mint-button "🎨 Mint NFT"])]

     ;; Display replies thread
     (when (seq (:replies cast))
       [:div.replies-thread
        (for [reply (:replies cast)]
          ^{:key (:id reply)}
          [:div.reply-item
           [:div.reply-author (if (= (:player-fid reply) player-fid) "You" "anon")]
           [:div.reply-text (:text reply)]])])]))

;; ============================================================================
;; FEED VIEW
;; ============================================================================

(defn feed-view [game app-state]
  (let [visible-casts (game/get-visible-casts game)
        seen-ids (:seen-cast-ids @app-state)
        current-ids (set (map :id visible-casts))
        new-ids (set/difference current-ids seen-ids)]
    ;; Mark new casts as seen after a brief delay (allows animation to play)
    (when (seq new-ids)
      (js/setTimeout
       #(swap! app-state update :seen-cast-ids set/union new-ids)
       500))  ;; After animation completes
    
    [:div.feed-container
     [:h2 "📱 Timeline"]
     [:div.feed-controls
      [:p.feed-info
       "Showing " (count visible-casts) " active casts"]]

     [:div.feed-list
      (if (seq visible-casts)
        (for [cast visible-casts]
          ^{:key (:id cast)}
          [cast-card cast app-state])
        [:div.empty-feed
         [:p "🎯 Click the button above to generate your first cast!"]])]]))

;; ============================================================================
;; STATS PANEL
;; ============================================================================

(defn stats-panel [game]
  (let [player (:player game)
        stats (:stats player)
        player-stats (game/get-player-stats game)
        quality-breakdown (:quality-breakdown player-stats)]
    [:div.stats-panel
     [:h3 "📊 Statistics"]

     [:div.stats-section
      [:h4 "Progress"]
      [:div.stat-row
       [:span "Total XP"]
       [:span.stat-value (game/format-number (:xp player))]]
      [:div.stat-row
       [:span "XP to Next"]
       [:span.stat-value (game/format-number (:xp-to-next player-stats))]]
      [:div.stat-row
       [:span "Progress"]
       [:span.stat-value (Math/floor (:xp-progress player-stats)) "%"]]]

     [:div.stats-section
      [:h4 "Activity"]
      [:div.stat-row
       [:span "Total Casts"]
       [:span.stat-value (:total-casts stats)]]
      [:div.stat-row
       [:span "Active Casts"]
       [:span.stat-value (:active-casts player-stats)]]
      [:div.stat-row
       [:span "Bangers"]
       [:span.stat-value (:total-bangers stats)]]
      [:div.stat-row
       [:span "Total Likes"]
       [:span.stat-value (:total-likes-received stats)]]]

     [:div.stats-section
      [:h4 "Cast Quality"]
      (for [[quality count] quality-breakdown]
        (let [tier (get config/quality-tiers quality)]
          ^{:key quality}
          [:div.stat-row
           [:span (:emoji tier) " " (:name tier)]
           [:span.stat-value count]]))]

     [:div.stats-section
      [:h4 "Time"]
      [:div.stat-row
       [:span "Playtime"]
       [:span.stat-value (game/format-time (:game-time stats))]]]]))

;; ============================================================================
;; LEVEL INFO PANEL
;; ============================================================================

(defn level-info-panel [game]
  (let [level (:level (:player game))
        features (config/get-level-features level)
        next-milestone (first (filter #(> % level) (sort (keys config/level-features))))]
    [:div.level-info-panel
     [:h3 "🎮 Level " level " Features"]

     [:div.feature-list
      [:div.feature-item
       [:span (if (:can-share-level? features) "✅" "🔒")]
       [:span "Share Progress"]]

      [:div.feature-item
       [:span (if (:can-cast-to-fc? features) "✅" "🔒")]
       [:span "Cast to Farcaster"]]

      [:div.feature-item
       [:span (if (:ai-generation? features) "✅" "🔒")]
       [:span "AI-Generated Casts"]]

      (when (:memory-size features)
        [:div.feature-item
         [:span "🧠"]
         [:span "Memory: " (:memory-size features) " casts"]])]

     (when next-milestone
       [:div.next-milestone
        [:p "🎯 Next milestone: Level " next-milestone]
        (let [next-features (config/get-level-features next-milestone)]
          [:div.feature-preview
           (when (and (:can-cast-to-fc? next-features)
                      (not (:can-cast-to-fc? features)))
             [:p "• Unlock casting to Farcaster"])
           (when (and (:ai-generation? next-features)
                      (not (:ai-generation? features)))
             [:p "• Unlock AI cast generation"])
           (when (:memory-size next-features)
             [:p "• Memory capacity: " (:memory-size next-features) " casts"])])])]))

;; ============================================================================
;; STATS OVERLAY (MODAL)
;; ============================================================================

(defn stats-overlay [game app-state]
  [:div.stats-overlay
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (swap! app-state assoc :stats-panel-open? false))}
   [:div.stats-overlay-content
    {:on-click (fn [e] (.stopPropagation e))}
    [:div.stats-overlay-header
     [:h2 "📊 Stats & Features"]
     [:button.stats-close-button
      {:on-click #(swap! app-state assoc :stats-panel-open? false)
       :title "Close"}
      "×"]]
    [:div.stats-overlay-body
     [level-info-panel game]
     [stats-panel game]]]])

;; ============================================================================
;; MAIN GAME VIEW
;; ============================================================================

;; ============================================================================
;; NOTIFICATION SYSTEM
;; ============================================================================

(defn notification-toast [notification app-state]
  "Render a single notification toast"
  (let [id (:id notification)
        type (:type notification)
        username (:username notification)
        message (:message notification)]
    [:div.notification-toast
     {:class (str "notification-" (name type))
      :key id}
     [:div.notification-content
      [:div.notification-icon
       (case type
         :like "❤️"
         :reply "💬"
         :banger "🔥"
         "📢")]
      [:div.notification-text
       [:div.notification-username username]
       [:div.notification-message message]]]
     [:button.notification-close
      {:on-click #(swap! app-state update :notifications
                         (fn [notifs] (remove (fn [n] (= (:id n) id)) notifs)))}
      "×"]]))

(defn notification-container [app-state]
  "Container for notifications - keep only latest 5, remove oldest when > 5"
  (let [all-notifications (:notifications @app-state)
        notifications (take-last 5 all-notifications)]  ;; Keep only latest 5
    (when (seq notifications)
      [:div.notification-container
       (for [notification notifications]
         ^{:key (:id notification)}
         [notification-toast notification app-state])])))

(defn game-view [state app-state]
  (let [game (:game state)]
    [:div.game-container
     ;; Pinned top section
     [:div.pinned-top-section
      [header state app-state]
      [notification-container app-state]]
     
     ;; Scrollable content
     [:div.game-content
      [:div.main-panel
       [feed-view game app-state]]]
     
     ;; Stats panel overlay (toggleable)
     (when (:stats-panel-open? @app-state)
       [stats-overlay game app-state])]))
