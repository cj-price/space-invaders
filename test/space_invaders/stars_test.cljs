(ns space-invaders.stars-test
  (:require
   [cljs.test :refer [deftest is]]
   [space-invaders.stars :as stars]))

(deftest gen-star-test
  (let [screen-width 1000
        [id {:keys [rotate scale speed x y]}] (stars/gen-star screen-width)]
    (is (symbol? id))
    (is (<= 0 rotate 359))
    (is (#{0.75 1 1.25} scale))
    (is (#{0.25 0.5 0.75} speed))
    (is (<= 0 x screen-width))
    (is (= y -8))))
