(ns space-invaders.invaders
  (:require
   [re-frame.core :as rf]
   [space-invaders.util :refer [element-in-bounds?]]))

(def INVADER_SPEED 8)

(def DIRECTION_FNS
  {:left -
   :right +})

(defn- gen-invader []
  [(gensym "invader") {:state :alive}])

(defn- invader [{:keys [state]}]
  (if (= state :alive)
    [:img.invader {:src "images/invader.svg"}]
    [:div.invader]))

(defn view []
  [:div#invaders {:style {:transform (str "translate(" @(rf/subscribe [::x]) "px)")}}
   (for [[id v] @(rf/subscribe [::invaders])]
     ^{:key id}
     [invader v])])

(rf/reg-event-fx
 ::spawn-invaders
 (rf/inject-cofx :screen)
 (fn [{:keys [db] {:keys [width]} :screen} _]
   {:db
    (assoc db
           ::invaders (into {} (map (fn [_] (gen-invader)) (range 15)))
           ::x (- (/ width 2) (/ 325 2)))}))

(defn- update-invaders-x [db direction]
  (update db ::x #((direction DIRECTION_FNS) % INVADER_SPEED)))

(rf/reg-event-fx
 ::move-invaders
 (rf/inject-cofx :elements {:root :getElementById
                            :invaders :getElementById})
 (fn [{:keys [db]
       {::keys [direction]} :db
       {:keys [invaders root]} :elements} _]
   {:db (if (element-in-bounds? invaders root)
          (update-invaders-x db direction)
          (let [direction (if (= direction :left) :right :left)]
            (-> db
                (update-invaders-x direction)
                (assoc ::direction direction))))}))

(rf/reg-sub
 ::invaders
 (fn [db]
   (::invaders db)))

(rf/reg-sub
 ::x
 (fn [db]
   (::x db)))
