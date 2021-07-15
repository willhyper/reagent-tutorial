(ns cljs-tutorial.views
  (:require
   [cljs-tutorial.utils :as utils]
   ))

(def R 100) ; Ray distance
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

(defn drawLines [canvas lines]
  (let [ctx (.getContext canvas "2d")]
    (set! (. ctx -fillStyle) "black")
    (doseq [[[xs ys] [xe ye]] lines]
      (doto ctx
        (.beginPath) (.moveTo xs ys) (.lineTo xe ye) (.stroke)))))

(defn drawRects [canvas rects]
  (let [ctx (.getContext canvas "2d")]
    (set! (. ctx -fillStyle) "black")
    (doseq [[[xs ys] [w h]] rects]
      (.fillRect ctx xs ys w h))))

(defn cameraRays [cam]
  (let [{x :x y :y cangle :angle} cam
        rays (for [ang (range (- cangle 30) (+ cangle 30) 0.5)]
               [(* R (Math/cos (* deg2rad ang)))
                (* R (Math/sin (* deg2rad ang)))])
        raysAbs (map (fn [[rx ry]] [[x y] [(+ x rx) (+ y ry)]]) rays)]
    (map (fn [ray] (utils/intersects ray walls)) raysAbs)))

(defn drawCam [canvas cam]
  (let [ctx (.getContext canvas "2d")
        {x :x y :y } cam
        raysIntersected (cameraRays cam)]
    (set! (. ctx -fillStyle) "black")
    (.fillRect ctx (- x 2) (- y 2) 5 5)
    (drawLines canvas raysIntersected)))

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

  
(defn canvas_track_mouse []
  [:div
   {:onMouseMove (fn [e]
                   (clearCanvas @fov_canvas)
                   (let [rect (.getBoundingClientRect @fov_canvas)
                         top (.-top rect) left (.-left rect)
                         w (.-width rect) h (.-height rect)
                         centerx (/ w 2) centery (/ h 2)

                         mx (.-clientX e) my (.-clientY e) ; absolute mouse x y coordinate
                         mox (- mx left) moy (- my top) ; mouse x y relative to fov origin
                         mcx (- mox centerx) mcy (- moy centery) ; mouse x y relative to fov center in pixel
                         angle (* 60 (/ mcx w)) ; range of (/mcx w) is (-0.5 0.5). *60 gives (-30 30) as viewing angle range in degree

                         camRays (cameraRays @cam)
                         raysDistance (map (fn [[P Q]] (utils/distance P Q)) camRays)
                         wallHeights (map (fn [dist] (* h (- 1 (/ dist R)))) raysDistance)
                         wallWidth (/ w (count wallHeights))
                         wallWidthStarts (range 0 w wallWidth)
                         wallRects (map (fn[wallWStart wallH]                            
                                          [[wallWStart (- (/ h 2) (/ wallH 2))]
                                           [wallWidth wallH]]) wallWidthStarts wallHeights)
                         ]
                     (swap! mouse assoc :x mcx :y mcy)
                     
                     (moveCam maze_canvas cam angle)

                     (drawRects @fov_canvas wallRects)
                     )

                   (clearCanvas @maze_canvas)
                   (drawLines @maze_canvas walls)
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
