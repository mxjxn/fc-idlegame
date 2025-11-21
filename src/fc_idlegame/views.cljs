(ns fc-idlegame.views
  (:require [reagent.core :as r]
            [fc-idlegame.game :as game]
            [fc-idlegame.config :as config]))

;; ============================================================================
;; LOADING VIEW
;; ============================================================================

(defn loading-view []
  [:div.loading
   [:div.spinner "⏳"]
   [:h2 "Initializing Farcaster Idle Game..."]])

;; ============================================================================
;; HEADER COMPONENT
;; ============================================================================

(defn header [{:keys [farcaster game]}]
  (let [player (:player game)
        stats (game/get-player-stats game)]
    [:header.game-header
     [:div.header-top
      [:h1 "💬 Farcaster Idle Game"]
      [:div.player-info
       [:span.username (:display-name farcaster)]
       (when (:fid farcaster)
         [:span.fid " (FID: " (:fid farcaster) ")"])]]
     [:div.header-stats
      [:div.stat-badge
       [:span.stat-label "Level"]
       [:span.stat-value (:level player)]]
      [:div.stat-badge
       [:span.stat-label "Total Casts"]
       [:span.stat-value (get-in player [:stats :total-casts])]]
      [:div.stat-badge
       [:span.stat-label "Bangers"]
       [:span.stat-value (get-in player [:stats :total-bangers])]]
      [:div.stat-badge
       [:span.stat-label "Active Casts"]
       [:span.stat-value (:active-casts stats)]]]]))

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
      [:span.xp-remaining (game/format-number xp-to-next) " XP to next level"]]
     [:div.xp-bar-container
      [:div.xp-bar {:style {:width (str progress "%")}}]]]))

;; ============================================================================
;; CAST GENERATOR (CLICK BUTTON)
;; ============================================================================

(defn cast-generator [app-state]
  (let [game (:game @app-state)
        player (:player game)
        last-cast (last (:casts game))]
    [:div.cast-generator
     [:h2 "📝 Generate Casts"]
     [:div.generator-content
      [:button.cast-button
       {:on-click #(swap! app-state update :game game/click)}
       "🎯 Click to Cast!"]

      (when last-cast
        [:div.last-cast-preview
         [:div.cast-quality
          (get-in config/quality-tiers [(:quality last-cast) :emoji])
          " "
          (get-in config/quality-tiers [(:quality last-cast) :name])]
         [:div.cast-text "\"" (:text last-cast) "\""]])]]))

;; ============================================================================
;; CAST CARD COMPONENT
;; ============================================================================

(defn cast-card [cast app-state]
  (let [quality-tier (get config/quality-tiers (:quality cast))
        age-seconds (/ (- (game/now) (:created-at cast)) 1000)
        age-display (cond
                      (< age-seconds 60) "just now"
                      (< age-seconds 3600) (str (Math/floor (/ age-seconds 60)) "m ago")
                      (< age-seconds 86400) (str (Math/floor (/ age-seconds 3600)) "h ago")
                      :else (str (Math/floor (/ age-seconds 86400)) "d ago"))]
    [:div.cast-card
     {:class (str "quality-" (name (:quality cast))
                  (when (:is-banger? cast) " banger"))}

     [:div.cast-header
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
      [:button.like-button
       {:on-click #(swap! app-state update :game game/like-cast (:id cast))}
       "❤️ " (:likes cast)]

      [:div.cast-stats
       [:span.replies-count "💬 " (count (:replies cast))]]

      (when (:is-banger? cast)
        [:button.share-button "📤 Share"])

      (when (and (:is-banger? cast) (not (:nft-minted? cast)))
        [:button.mint-button "🎨 Mint NFT"])]]))

;; ============================================================================
;; FEED VIEW
;; ============================================================================

(defn feed-view [game app-state]
  (let [visible-casts (game/get-visible-casts game)]
    [:div.feed-container
     [:h2 "📱 Your Casts"]
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
       [:span "Manual Clicks"]
       [:span.stat-value (:total-clicks stats)]]
      [:div.stat-row
       [:span "Total Casts"]
       [:span.stat-value (:total-casts stats)]]
      [:div.stat-row
       [:span "Total Likes"]
       [:span.stat-value (:total-likes-received stats)]]
      [:div.stat-row
       [:span "Bangers"]
       [:span.stat-value (:total-bangers stats)]]]

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
;; MAIN GAME VIEW
;; ============================================================================

(defn game-view [state app-state]
  (let [game (:game state)]
    [:div.game-container
     [header state]

     [:div.game-content
      [:div.main-panel
       [xp-progress-bar game]
       [cast-generator app-state]
       [feed-view game app-state]]

      [:div.side-panel
       [level-info-panel game]
       [stats-panel game]]]]))
