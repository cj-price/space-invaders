(ns space-invaders.stars
  (:require
   [re-frame.core :as rf]))

(def STAR_SIZES
  {:sm {:scale 0.75
        :speed 0.25}
   :md {:scale 1
        :speed 0.5}
   :lg {:scale 1.25
        :speed 0.75}})

(defn- gen-star [screen-width]
  [(gensym "star") (merge {:rotate (rand-int 360)
                           :x (rand-int screen-width)
                           :y -8}
                          (-> STAR_SIZES seq rand-nth second))])

(defn- star [{:keys [rotate scale x y]}]
  [:img.star {:src "images/star.svg"
              :style {:transform (str " translate(" x "px," y "px) rotate(" rotate "deg)scale(" scale ")")}}])

(defn view []
  [:<>
   (for [[id v] @(rf/subscribe [::stars])]
     ^{:key id}
     [star v])])

(rf/reg-event-fx
 ::spawn-stars
 [(rf/inject-cofx :screen)]
 (fn [{:keys [db] {:keys [width]} :screen} _]
   {:db (update db ::stars conj (gen-star width))
    :fx [[:dispatch-later {:ms (+ (rand-int 2000) 1000) :dispatch [::spawn-stars]}]]}))

(rf/reg-event-fx
 ::update-star-positions
 (fn [{:keys [db]} _]
   {:fx [[:dispatch (into [::-update-star-position] (keys (::stars db)))]]}))

(rf/reg-event-fx
 ::-update-star-position
 [(rf/inject-cofx :screen)]
 (fn [{:keys [db] {:keys [height]} :screen} [_ id & rest]]

   (when-let [{:keys [speed y]} (get-in db [::stars id])]
     {:db (if (> y height)
            (update db ::stars #(dissoc % id))
            (update-in db [::stars id :y] (partial + speed)))
      :fx [[:dispatch (into [::-update-star-position] rest)]]})))

(rf/reg-sub
 ::stars
 (fn [db]
   (::stars db)))
