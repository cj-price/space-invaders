(ns space-invaders.core
  (:require
   [oops.core :refer [ocall ocall+ oget]]
   [reagent.dom :as rd]
   [re-frame.core :as rf]
   [space-invaders.invaders :as invaders]
   [space-invaders.ship :as ship]
   [space-invaders.stars :as stars]))

(defn- base []
  [:<>
   [stars/view]
   [invaders/view]
   [ship/view]])

(defn ^:dev/after-load render []
  (rd/render [base] (ocall js/document :getElementById "root")))

(defn start []
  (rf/dispatch-sync [::init])
  (render))

(rf/reg-event-fx
 ::init
 (fn [_ _]
   ;; Initial DB
   ;; TODO spec out db and add global interceptor to check db
   {:db {::invaders/direction :right
         ::invaders/invaders {}
         ::invaders/x 0
         ::ship/moving-left? false
         ::ship/moving-right? false
         ::stars/stars {}}
    :fx [[:dispatch [::stars/spawn-stars]]
         [:dispatch [::invaders/spawn-invaders]]
         [:dispatch [::ship/spawn-ship]]
         [:dispatch [::game-loop]]]}))

(rf/reg-event-fx
 ::game-loop
 (fn [_ _]
   {:fx [[:dispatch [::ship/movement-handler]]
         [:dispatch [::stars/update-star-positions]]
         [:dispatch [::invaders/move-invaders]]
         [:dispatch-later {:ms 32 :dispatch [::game-loop]}]]}))

(rf/reg-cofx
 :screen
 (fn [cofx _]
   (assoc cofx :screen {:height (oget js/document :documentElement.clientHeight)
                        :width (oget js/document :documentElement.clientWidth)})))

(rf/reg-cofx
 :elements
 (fn [cofx elements]
   (assoc cofx :elements (reduce-kv
                          (fn [acc k v]
                            (conj acc [k (ocall+ js/document v (name k))]))
                          {} elements))))
