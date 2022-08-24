(ns space-invaders.bullet
  (:require
   [re-frame.core :as rf]
   #_[space-invaders.ship :as ship]))

(defn view []
  (let [x (rf/subscribe [::x])
        y (rf/subscribe [::y])]
    [:div#bullets
     [:div.bullet {:style {:transform (str "translate(" @x "px, " @y "px)")}}]]))

(rf/reg-event-fx
 ::shoot-bullet
 (rf/inject-cofx :screen)
 (fn [{{:keys [height]} :screen
       db :db} _]
   (let [x (:space-invaders.ship/x db)]
     {:db (assoc db
                 ::x x
                 ::y (- height 40))})))

(rf/reg-event-fx
 ::advance-bullets
 (fn []))

(rf/reg-sub
 ::x
 (fn [db]
   (::x db)))

(rf/reg-sub
 ::y
 (fn [db]
   (::y db)))
