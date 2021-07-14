(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [cljs-tutorial.subs :as subs]
   ))


(def deg2rad (/ Math/PI 180))
(def _canvas (atom {}))
(def _canvas_cam (atom {}))


(def cam (atom {:x 100 :y 100 :angle 0}))
(def mouse (atom {:x 10 :y 10}))
(def fov (atom {}))
(def maze (atom {}))

(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (let [ctx (.getContext @_canvas "2d")
                         w (.-width @_canvas) h (.-height @_canvas)
                         wmaze (.-width @_canvas_cam) hmaze (.-height @_canvas_cam)
                         {top :top left :left right :right bottom :bottom centerx :centerx centery :centery} @fov ; absolute fov coordinate 
                         mx (.-clientX e) my (.-clientY e) ; absolute mouse x y coordinate
                         mox (- mx left) moy (- my top) ; mouse x y relative to fov origin
                         mcx (- mox centerx) mcy (- moy centery) ; mouse x y relative to fov center in pixel

                         cangle (:angle @cam) ; in degree
                         angle (* 60 (/ mcx w)) ; range of (/mcx w) is (-0.5 0.5). *60 gives (-30 30) as viewing angle range in degree

                         canglenew  (+ cangle (/ angle 10)) ; rule of assigning new camera angle: in 10 steps cangle to reach angle
                         cxnew (min wmaze (max 0 (+ (:x @cam) (Math/cos (* canglenew deg2rad)))))
                         cynew (min hmaze (max 0 (+ (:y @cam) (Math/sin (* canglenew deg2rad)))))
                         ] 
                     (swap! mouse assoc :x mcx :y mcy)
                     (swap! cam assoc :x cxnew :y cynew :angle canglenew)

                     (set! (. ctx -fillStyle) "black")

                     (.clearRect ctx 0 0 w h)

                     (doto ctx
                       (.beginPath) (.moveTo centerx centery) (.lineTo mox moy) (.stroke))
                     
                     
                     )
                     (let [ctx (.getContext @_canvas_cam "2d")
                           w (.-width @_canvas_cam) h (.-height @_canvas_cam)
                           {x :x y :y cangle :angle} @cam
                           R 100
                           rayx (* R (Math/cos (* deg2rad cangle)))
                           rayy (* R (Math/sin (* deg2rad cangle)))]

                       (set! (. ctx -fillStyle) "black")

                       (.clearRect ctx 0 0 w h)
                       (.fillRect ctx (- x 2) (- y 2) 5 5)
                       (doto ctx
                         (.beginPath) (.moveTo x y) (.lineTo (+ x rayx) (+ y rayy)) (.stroke)))
                     )}
   [:canvas {:ref (fn [c]
                    (reset! _canvas c)

                    ; Caveats with callback refs: you may get nil
                    ; https://reactjs.org/docs/refs-and-the-dom.html
                    (if (nil? c)
                      (); do nothing
                      (let [rect (.getBoundingClientRect c)
                            t (.-top rect) r (.-right rect)
                            b (.-bottom rect) l (.-left rect)]
                        (swap! fov assoc :top t :left l :right r :bottom b :centerx (/ (- r l) 2) :centery (/ (- b t) 2)))))
             :width 1200 :height 600 ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
             :style {:background-color "lightblue"
                     :width 1200 :height 600}
             :tabIndex 1}]
   [:canvas {:ref (fn [c]
                    (reset! _canvas_cam c)

                    ; Caveats with callback refs: you may get nil
                    ; https://reactjs.org/docs/refs-and-the-dom.html
                    (if (nil? c)
                      (); do nothing
                      (let [rect (.getBoundingClientRect c)
                            t (.-top rect) r (.-right rect)
                            b (.-bottom rect) l (.-left rect)]
                        (swap! maze assoc :top t :left l :right r :bottom b))))
             :width 400 :height 200 ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
             :style {:background-color "yellow"
                     :width 400 :height 200}
             :tabIndex 2}]
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
