(ns space-invaders.util
  (:require
   [oops.core :refer [ocall oget]]))

(defn element-in-bounds?
  "Checks to see if element 1 is partially, or fully out of bounds of element 2.
  Returns true if it is in bounds.
  Returns false if it is partially, or fully out of bounds.

  :x-adjustment optional param modifies the first param x value based on :y-adjustment-fn.
  :x-adjustment defaults to 0 while :x-adjustment-fn defaults to +

  Likewise, :y-adjustment optional param modifies the first param y value based on :y-adjustment-fn.
  :y-adjustment defaults to 0 while :y-adjustment-fn defaults to +

  Examples:
  user=> (element-in-bounds? dom-node1 dom-node2)
  ;; => true

  user=> (element-in-bounds? dom-node1 dom-node2 :y-adjustment 10 :y-adjustment-fn +)
  ;; => false"
  [el1 el2 & {x-adj :x-adjustment
              y-adj :y-adjustment
              x-adj-fn :x-adjustment-fn
              y-adj-fn :y-adjustment-fn
              :or {x-adj 0 y-adj 0 x-adj-fn + y-adj-fn +}}]

  (let [rect1 (ocall el1 :getBoundingClientRect)
        rect2 (ocall el2 :getBoundingClientRect)

        rect1-height (oget rect1 :height)
        rect1-width (oget rect1 :width)
        rect1-x (x-adj-fn (oget rect1 :x) x-adj)
        rect1-y (y-adj-fn (oget rect1 :y) y-adj)

        rect2-height (oget rect2 :height)
        rect2-width (oget rect2 :width)
        rect2-x (oget rect2 :x)
        rect2-y (oget rect2 :y)]

    (and (<= (+ rect1-x rect1-width) (+ rect2-width rect2-x))   ;; Inside from the right
         (>= rect1-x rect2-x)                                   ;; Inside from the left
         (<= (+ rect1-y rect1-height) (+ rect2-height rect2-y)) ;; Inside from the bottom
         (>= rect1-y rect2-y))))                                ;; Inside from the top
