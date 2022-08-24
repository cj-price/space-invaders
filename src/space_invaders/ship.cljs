(ns space-invaders.ship
  (:require
   [oops.core :refer [oget oset!]]
   [re-frame.core :as rf]
   [space-invaders.bullet :as bullet]
   [space-invaders.util :refer [element-in-bounds?]]))

(def SHIP_SPEED 18)

(defn view []
  [:img#ship {:src "images/ship.svg"
              :style {:transform (str "translate(" @(rf/subscribe [::x]) "px)")}}])

(rf/reg-event-db
 ::start-moving-right
 (fn [db _]
   (assoc db ::moving-right? true)))

(rf/reg-event-db
 ::start-moving-left
 (fn [db _]
   (assoc db ::moving-left? true)))

(rf/reg-event-db
 ::stop-moving-right
 (fn [db _]
   (assoc db ::moving-right? false)))

(rf/reg-event-db
 ::stop-moving-left
 (fn [db _]
   (assoc db ::moving-left? false)))

(defn- movement-handler [db ship-el root-el f]
  (if (element-in-bounds? ship-el root-el :x-adjustment SHIP_SPEED :x-adjustment-fn f)
    (update db ::x #(f % SHIP_SPEED))
    db))

(rf/reg-event-fx
 ::movement-handler
 (rf/inject-cofx :elements {:root :getElementById
                            :ship :getElementById})
 (fn [{{::keys [moving-left? moving-right?] :as db} :db
       {:keys [ship root]} :elements} _]
   {:db (case [moving-left? moving-right?]
          [true false] (movement-handler db ship root -)
          [false true] (movement-handler db ship root +)
          db)}))

(rf/reg-event-fx
 ::spawn-ship
 (rf/inject-cofx :screen)
 (fn [{:keys [db] {:keys [width]} :screen} _]
   {:db (assoc db ::x (- (/ width 2) (/ 55 2)))
    ::setup-controls nil}))

(rf/reg-fx
 ::setup-controls
 (fn [_]
   (-> js/document
       (oset! :onkeydown
              (fn [e]
                (case (oget e :key)
                  "ArrowLeft" (rf/dispatch [::start-moving-left])
                  "ArrowRight" (rf/dispatch [::start-moving-right])
                  " " (rf/dispatch [::bullet/shoot-bullet])
                  :noop)))
       (oset! :onkeyup
              (fn [e]
                (case (oget e :key)
                  "ArrowLeft" (rf/dispatch [::stop-moving-left])
                  "ArrowRight" (rf/dispatch [::stop-moving-right])
                  :noop))))))

(rf/reg-sub
 ::x
 (fn [db]
   (::x db)))
