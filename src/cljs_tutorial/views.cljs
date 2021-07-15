(ns cljs-tutorial.views
  (:require
   [cljs-tutorial.utils :as utils]
   ))


(def deg2rad (/ Math/PI 180))
(defonce fovDim {:width 1200 :height 600})
(def fov_canvas (atom {}))
(defonce mazeDim {:width 400 :height 200})
(def maze_canvas (atom {}))

(def cam (atom {:x 100 :y 100 :angle 0}))
(def mouse (atom {:x 10 :y 10}))

(defonce walls
  (let [{w :width h :height} mazeDim]
    (repeatedly 10 (fn [] [[(rand-int w) (rand-int h)] [(rand-int w) (rand-int h)]]))))

(defn intersect [[start end :as ray] wall]
  (if (utils/intersect? ray wall) [start (utils/intersection ray wall)] ray))

(defn intersects [ray walls]
  (->> walls (map #(intersect ray %)) (apply min-key #(apply utils/distance %))))


(defn drawCam [canvas cam]
  (let [ctx (.getContext canvas "2d")
        {x :x y :y cangle :angle} cam
        R 100
        rays (for [ang (range (- cangle 30) (+ cangle 30) 5)]
               [(* R (Math/cos (* deg2rad ang)))
                (* R (Math/sin (* deg2rad ang)))])
        raysAbs (map (fn [[rx ry]] [[x y] [(+ x rx) (+ y ry)]]) rays)
        raysIntersected (map (fn [ray] (intersects ray walls)) raysAbs)]
    (set! (. ctx -fillStyle) "black")
    (.fillRect ctx (- x 2) (- y 2) 5 5)
    (doseq [[[xs ys] [xe ye]] raysIntersected]
      (doto ctx
        (.beginPath) (.moveTo xs ys) (.lineTo xe ye) (.stroke)))))

(defn clearCanvas [canvas]
  (let [ctx (.getContext canvas "2d") w (.-width canvas) h (.-height canvas)]
    (.clearRect ctx 0 0 w h)))

(defn moveCam [canvas cam angle]
  (let [wmaze (.-width @canvas) hmaze (.-height @canvas)
        cangle (:angle @cam) ; in degree    
        canglenew  (+ cangle (/ angle 10)) ; rule of assigning new camera angle: in 10 steps cangle to reach angle
        cxnew (min wmaze (max 0 (+ (:x @cam) (Math/cos (* canglenew deg2rad)))))
        cynew (min hmaze (max 0 (+ (:y @cam) (Math/sin (* canglenew deg2rad)))))]
    (swap! cam assoc :x cxnew :y cynew :angle canglenew))
  )

(defn drawWalls [canvas walls]
  (let [ctx (.getContext @canvas "2d")]
    (doseq [[[xs ys] [xe ye]] walls]
      (doto ctx
        (.beginPath) (.moveTo xs ys) (.lineTo xe ye) (.stroke))))
    )
  
(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (clearCanvas @fov_canvas)
                   (let [ctx (.getContext @fov_canvas "2d")
                         rect (.getBoundingClientRect @fov_canvas)
                         top (.-top rect) left (.-left rect)
                         w (.-width rect) h (.-height rect)
                         centerx (/ w 2) centery (/ h 2)

                         mx (.-clientX e) my (.-clientY e) ; absolute mouse x y coordinate
                         mox (- mx left) moy (- my top) ; mouse x y relative to fov origin
                         mcx (- mox centerx) mcy (- moy centery) ; mouse x y relative to fov center in pixel
                         angle (* 60 (/ mcx w)) ; range of (/mcx w) is (-0.5 0.5). *60 gives (-30 30) as viewing angle range in degree
                         ] 
                     (swap! mouse assoc :x mcx :y mcy)
                     
                     (moveCam maze_canvas cam angle)

                     (set! (. ctx -fillStyle) "black")
                     (doto ctx
                       (.beginPath) (.moveTo centerx centery) (.lineTo mox moy) (.stroke)))

                   (clearCanvas @maze_canvas)
                   (drawWalls maze_canvas walls)
                   (drawCam @maze_canvas @cam))}
   (let [{w :width h :height} fovDim]
     [:canvas {:ref (fn [c]
                      (reset! fov_canvas c)

                    ; Caveats with callback refs: you may get nil
                    ; https://reactjs.org/docs/refs-and-the-dom.html
                      )
               :width w :height h ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
               :style {:background-color "lightblue"
                       :width w :height h}
               :tabIndex 1}])
   (let [{w :width h :height} mazeDim]
     [:canvas {:ref (fn [c]
                      (reset! maze_canvas c))
               :width w :height h ; https://stackoverflow.com/questions/4938346/canvas-width-and-height-in-html5
               :style {:background-color "yellow"
                       :width w :height h}
               :tabIndex 2}])
   [:br]
   "mouse " mouse
   [:br]
   "camera " cam
   
   ]
  )

(defn main-panel []
  [canvas_track_mouse]
  )
