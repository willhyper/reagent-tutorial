(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [cljs-tutorial.subs :as subs]
   ))

(def _canvas (atom {}))

(def cam (atom {:angle 0}))
(def mouse (atom {:x 10 :y 10}))
(def fov (atom {}))
(def debug (atom {}))

(defn wall-height [H angle]
  (->> angle (* (/ Math/PI 180)) Math/atan (* (/ 2 Math/PI))  (- 1) (* (/ H 2))))

(defn wall-coordinate [H W angle]
  (let [wh (wall-height H angle)]
    [(+ (/ W 2) (* angle (/ W 120))) 
     (- (/ H 2) (/ wh 2)) 
     15
     wh]))

(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (let [ctx (.getContext @_canvas "2d")
                         w (.-width @_canvas) h (.-height @_canvas)
                         {top :top left :left right :right bottom :bottom centerx :centerx centery :centery} @fov ; absolute fov coordinate 
                         mx (.-clientX e) my (.-clientY e) ; absolute mouse x y coordinate
                         mox (- mx top) moy (- my left) ; mouse x y relative to fov origin
                         mcx (- mox centerx) mcy (- moy centery) ; mouse x y relative to fov center in pixel

                         ; rule of assigning new camera x y
                        ;;  cx (:x @cam) cy (:y @cam) ; camera x y relative to fov origin
                        ;;  dcx (- mox cx) dcy (- moy cy) ; mouse camera x y difference
                        ;;  cxnew (int (+ cx (/ dcx 5)))  cynew (int (+ cy (/ dcy 5))) 

                         cangle (:angle @cam) ; in degree
                         angle (* 60 (/ mcx w)) ; range of (/mcx w) is (-0.5 0.5). *60 gives (-30 30) as viewing angle range in degree
                         canglenew  (+ cangle (/ (- angle cangle) 10)) ; rule of assigning new camera angle: in 10 steps cangle to reach angle
                         ] 
                     (swap! mouse assoc :x mcx :y mcy)
                     (swap! cam assoc :angle canglenew)

                     (set! (. ctx -fillStyle) "black")

                     (.clearRect ctx 0 0 w h)

                     (doto ctx
                       (.beginPath) (.moveTo centerx centery) (.lineTo mox moy) (.stroke))
                     
                     
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
                        (swap! fov assoc :top t :left l :right r :bottom b :centerx (/ (- r l) 2) :centery (/ (- b t) 2)))))
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
