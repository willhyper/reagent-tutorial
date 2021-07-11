(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [cljs-tutorial.subs :as subs]
   ))

(def _canvas (atom {}))

(def cam (atom {:x 10 :y 10 :angle 0}))
(def mouse (atom {:x 10 :y 10}))
(def debug (atom {}))

(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (let [ctx (.getContext @_canvas "2d")
                         w (.-width @_canvas) h (.-height @_canvas)
                         mx (.-clientX e) my (.-clientY e)
                         cx (:x @cam) cy (:y @cam)
                         dx (- mx cx) dy (- my cy) ang (Math/atan (/ dy dx))
                         cxnew (+ cx (/ dx 20)) cynew (+ cy (/ dy 20))]
                     (swap! mouse assoc :x mx :y my)
                     (swap! cam assoc :angle ang :x cxnew :y cynew)

                     (set! (. ctx -fillStyle) "black")

                     (.clearRect ctx 0 0 w h)
                     (.fillRect ctx cx cy 10 10)))}
   [:canvas {:ref (fn [c]
                    (reset! _canvas c)

                    ; Caveats with callback refs: you may get nil
                    ; https://reactjs.org/docs/refs-and-the-dom.html
                    (if (nil? c)
                      (); do nothing
                      (let [rect (.getBoundingClientRect c)
                            t (.-top rect) r (.-right rect)
                            b (.-bottom rect) l (.-left rect)]
                        (swap! debug assoc :top t :left l :right r :bottom b ))))
             :width 1200 :height 600 ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
             :style {:background-color "lightblue"
                     :width 1200 :height 600}
             :tabIndex 1}]

   [:br]
   cam
   [:br]
   mouse
   ])

(defn main-panel []
  [canvas_track_mouse]
  )
