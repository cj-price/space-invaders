(ns space-invaders.bullet
  (:require
   [re-frame.core :as rf]))

(defn view []
  (let [bullets (rf/subscribe [::bullets])]
    (into [:div#bullets]
          (for [{:as bullet :keys [x y]} @bullets]
            [:div.bullet {:style {:transform (str "translate(" x "px, " y "px)")}}]))))

(rf/reg-event-fx
 ::shoot-bullet
 (rf/inject-cofx :screen)
 (fn [{{:keys [height]} :screen
       db :db} _]
   (let [x (:space-invaders.ship/x db)]
     {:db (update db ::bullets (fnil conj []) {:x x :y (- height 40)})})))

(rf/reg-event-fx
 ::advance-bullets
 (fn [{{::keys [bullets] :as db} :db}]
   {:db (assoc db ::bullets
               (vec
                (for [bullet bullets
                      :let [new-bullet (update bullet :y - 40)]
                      :when (pos? (:y new-bullet))]
                  new-bullet)))}))

(rf/reg-sub
 ::bullets
 (fn [db]
   (::bullets db)))

(rf/reg-sub
 ::x
 (fn [db]
   (::x db)))

(rf/reg-sub
 ::y
 (fn [db]
   (::y db)))
