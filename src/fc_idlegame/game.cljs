(ns fc-idlegame.game
  (:require [clojure.string :as str]))

;; Game State Definition
(def initial-game-state
  {:resources {:points 0
               :points-per-second 0}
   :buildings []
   :upgrades []
   :stats {:total-clicks 0
           :total-points-earned 0
           :game-time 0}
   :last-tick (.now js/Date)})

;; Building Definitions
(def building-types
  [{:id :clicker
    :name "Auto Clicker"
    :description "Automatically generates 1 point per second"
    :base-cost 10
    :base-production 1
    :cost-multiplier 1.15}

   {:id :generator
    :name "Point Generator"
    :description "Generates 5 points per second"
    :base-cost 100
    :base-production 5
    :cost-multiplier 1.15}

   {:id :factory
    :name "Point Factory"
    :description "Generates 25 points per second"
    :base-cost 1000
    :base-production 25
    :cost-multiplier 1.15}

   {:id :mega-factory
    :name "Mega Factory"
    :description "Generates 100 points per second"
    :base-cost 10000
    :base-production 100
    :cost-multiplier 1.15}])

;; Helper Functions
(defn get-building-type [building-id]
  (first (filter #(= (:id %) building-id) building-types)))

(defn calculate-building-cost [building-type count]
  (let [{:keys [base-cost cost-multiplier]} building-type]
    (Math/floor (* base-cost (Math/pow cost-multiplier count)))))

(defn count-buildings-of-type [buildings building-id]
  (count (filter #(= (:type %) building-id) buildings)))

(defn calculate-total-production [buildings]
  (reduce
   (fn [total building]
     (let [building-type (get-building-type (:type building))]
       (+ total (:base-production building-type))))
   0
   buildings))

;; Game Actions
(defn click [game-state]
  (-> game-state
      (update-in [:resources :points] inc)
      (update-in [:stats :total-clicks] inc)
      (update-in [:stats :total-points-earned] inc)))

(defn can-afford? [game-state cost]
  (>= (get-in game-state [:resources :points]) cost))

(defn purchase-building [game-state building-id]
  (let [building-type (get-building-type building-id)
        current-count (count-buildings-of-type (:buildings game-state) building-id)
        cost (calculate-building-cost building-type current-count)]
    (if (can-afford? game-state cost)
      (-> game-state
          (update-in [:resources :points] - cost)
          (update :buildings conj {:type building-id
                                    :purchased-at (.now js/Date)})
          (assoc-in [:resources :points-per-second]
                    (calculate-total-production
                     (conj (:buildings game-state)
                           {:type building-id}))))
      game-state)))

;; Game Loop Tick
(defn tick [game-state]
  (let [now (.now js/Date)
        last-tick (:last-tick game-state)
        delta-ms (- now last-tick)
        delta-seconds (/ delta-ms 1000)
        pps (get-in game-state [:resources :points-per-second])
        points-earned (* pps delta-seconds)]
    (-> game-state
        (update-in [:resources :points] + points-earned)
        (update-in [:stats :total-points-earned] + points-earned)
        (update-in [:stats :game-time] + delta-seconds)
        (assoc :last-tick now))))

;; Format numbers for display
(defn format-number [n]
  (cond
    (>= n 1000000000) (str (Math/floor (/ n 1000000)) "M")
    (>= n 1000000) (str (Math/floor (/ n 1000000)) "M")
    (>= n 1000) (str (Math/floor (/ n 1000)) "K")
    :else (str (Math/floor n))))

(defn format-time [seconds]
  (let [hours (Math/floor (/ seconds 3600))
        minutes (Math/floor (/ (mod seconds 3600) 60))
        secs (Math/floor (mod seconds 60))]
    (str hours "h " minutes "m " secs "s")))
