(ns fc-idlegame.views
  (:require [reagent.core :as r]
            [fc-idlegame.game :as game]))

;; Loading View
(defn loading-view []
  [:div.loading
   [:div.spinner "⏳"]
   [:h2 "Loading Farcaster Idle Game..."]])

;; Header Component
(defn header [{:keys [farcaster]}]
  [:header.game-header
   [:h1 "🎮 Farcaster Idle Game"]
   [:div.player-info
    [:span "Player: " (:display-name farcaster)]
    [:span " (FID: " (:fid farcaster) ")"]]])

;; Resources Display
(defn resources-display [resources]
  [:div.resources
   [:div.resource-card
    [:div.resource-label "Points"]
    [:div.resource-value (game/format-number (:points resources))]]
   [:div.resource-card
    [:div.resource-label "Per Second"]
    [:div.resource-value (game/format-number (:points-per-second resources)) "/s"]]])

;; Click Button
(defn click-button [app-state]
  [:div.click-area
   [:button.click-button
    {:on-click #(swap! app-state update :game game/click)}
    "🎯 Click for Points!"]])

;; Building Card Component
(defn building-card [building-type buildings app-state]
  (let [current-count (game/count-buildings-of-type buildings (:id building-type))
        cost (game/calculate-building-cost building-type current-count)
        points (get-in @app-state [:game :resources :points])
        can-afford? (>= points cost)]
    [:div.building-card
     {:class (when can-afford? "affordable")}
     [:div.building-header
      [:h3 (:name building-type)]
      [:span.building-count "Owned: " current-count]]
     [:p.building-description (:description building-type)]
     [:div.building-footer
      [:div.building-production
       "+" (game/format-number (:base-production building-type)) " /s"]
      [:button.buy-button
       {:disabled (not can-afford?)
        :on-click #(swap! app-state update :game game/purchase-building (:id building-type))}
       "Buy for " (game/format-number cost)]]]))

;; Buildings Section
(defn buildings-section [game-state app-state]
  [:div.buildings
   [:h2 "🏗️ Buildings"]
   [:div.buildings-grid
    (for [building-type game/building-types]
      ^{:key (:id building-type)}
      [building-card building-type (:buildings game-state) app-state])]])

;; Stats Section
(defn stats-section [stats]
  [:div.stats
   [:h3 "📊 Statistics"]
   [:div.stats-grid
    [:div.stat-item
     [:span.stat-label "Total Clicks:"]
     [:span.stat-value (:total-clicks stats)]]
    [:div.stat-item
     [:span.stat-label "Total Points:"]
     [:span.stat-value (game/format-number (:total-points-earned stats))]]
    [:div.stat-item
     [:span.stat-label "Game Time:"]
     [:span.stat-value (game/format-time (:game-time stats))]]]])

;; Main Game View
(defn game-view [state app-state]
  [:div.game-container
   [header state]
   [:div.game-content
    [:div.main-panel
     [resources-display (get-in state [:game :resources])]
     [click-button app-state]
     [buildings-section (:game state) app-state]]
    [:div.side-panel
     [stats-section (get-in state [:game :stats])]]]])
