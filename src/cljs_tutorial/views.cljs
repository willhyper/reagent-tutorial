(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [cljs-tutorial.subs :as subs]
   ))

(def _canvas (atom {}))

(def cam (atom {:x 10 :y 10 :angle 0}))
(def mouse (atom {:x 10 :y 10}))
(def fov (atom {}))
(def debug (atom {}))

(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (let [ctx (.getContext @_canvas "2d")
                         w (.-width @_canvas) h (.-height @_canvas)
                         {top :top left :left right :right bottom :bottom} @fov ; absolute fov coordinate 
                         mx (.-clientX e) my (.-clientY e) ; absolute mouse x y coordinate
                         mox (- mx top) moy (- my left) ; mouse x y relative to fov origin
                         cx (:x @cam) cy (:y @cam) ; camera x y relative to fov origin
                         dx (- mox cx) dy (- moy cy) ang (mod (* 180 (/ (Math/atan (/ dy dx) Math/PI))) 360) ; mouse camera x y difference
                         cxnew (int (+ cx (/ dx 5)))  cynew (int (+ cy (/ dy 5)))] ; rule of assigning new camera x y 
                     (swap! mouse assoc :x mox :y moy)
                     (swap! cam assoc :angle ang :x cxnew :y cynew)

                     (set! (. ctx -fillStyle) "black")

                     (.clearRect ctx 0 0 w h)
                     (.fillRect ctx cxnew cynew 5 5)
                     

                     ))}
   [:canvas {:ref (fn [c]
                    (reset! _canvas c)

                    ; Caveats with callback refs: you may get nil
                    ; https://reactjs.org/docs/refs-and-the-dom.html
                    (if (nil? c)
                      (); do nothing
                      (let [rect (.getBoundingClientRect c)
                            t (.-top rect) r (.-right rect)
                            b (.-bottom rect) l (.-left rect)]
                        (swap! fov assoc :top t :left l :right r :bottom b))))
             :width 1200 :height 600 ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
             :style {:background-color "lightblue"
                     :width 1200 :height 600}
             :tabIndex 1}]

   [:br]
   "mouse " mouse
   [:br]
   "camera " cam
   [:br]
   "fov " fov]
  )

(defn main-panel []
  [canvas_track_mouse]
  )
