(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :refer [atom]]
   [cljs-tutorial.subs :as subs]
   ))


(defn counter []
  [:div [:h1 ">>> " @(re-frame/subscribe [:view-key :cnt])]
   [:input {:type "button"
            :value "inc"
            :on-click #(re-frame/dispatch [:cnt-event inc])}]])


(defn mirror-text []
  (let [txt (re-frame/subscribe [:view-key :txt])]
    [:div @txt
     [:input {:type "text"
              :value @txt
              :on-change #(re-frame/dispatch [:txt-event (-> % .-target .-value)])}]])
  )

(def s (atom {:m 0 :M 100}))

(defn slider [m M]
  (let [_m (:m @s)
        _M (:M @s)]
    [:div
     [:input {:type "range" :min m :max M
              :style {:width "70%"}
              :value _m
              :on-change (fn [e]
                           (let [sv (.. e -target -value)]
                             (swap! s assoc :m sv :M (max _M sv))))}]
     [:br]
     [:input {:type "range" :min m :max M
              :style {:width "70%"}
              :value _M
              :on-change (fn [e]
                           (let [sv (.. e -target -value)]
                             (swap! s assoc :M sv :m (min _m sv))))}]
     @s]))

(defn uili [items]
  [:ui
   (for [item items] [:li "item " item])
   ])

(defn condition-color []
  (let [v (:M @s)
        color (cond (< v 30) ["green"]
                    (< v 60) ["orange"]
                    (< v 90) ["red"])]
    [:h1 {:style {:color color}} "color"]
    ))

(def rgb (atom {:r 127 :g 127 :b 127}))

(defn slide-color []
  [:div
   (for [c  [:r :g :b]]
     [:input {:type "range" :min 0 :max 255
              :style {:width "70%"}
              :value  (c @rgb)
              :on-change (fn [e] (let [v (.. e -target -value)
                                       vi (js/parseInt v)]
                                   (swap! rgb assoc c vi)))}])
   @rgb
   [:br]
   (let [[rh gh bh] (map  #(. (@rgb %) toString 16) [:r :g :b])
         {rr :r gg :g bb :b} @rgb
         color (str "#" rh gh bh)]
     [:div
      [:h1 {:style {:color color}} color]
      [:h2 rr " " gg " " bb]])
   ]
  )

(def coor (atom {}))
(def _canvas (atom {}))
(defn canvaz []
  [:div
   {:onMouseMove (fn [e]
                   (let [x (. e -clientX) y (. e -clientY)]
                     (swap! coor assoc :x x :y y)))
    :onMouseUp (fn [e]
                   (let [x (. e -clientX) y (. e -clientY)
                         ctx (.getContext @_canvas "2d")
                         gradient (. ctx createLinearGradient 0 0 x y)]

                     (doto gradient
                       (.addColorStop 0 "#424242")
                       (.addColorStop 0.5 "#111111")
                       (.addColorStop 1 "#e2e2e2"))
                     (set! (. ctx -fillStyle) gradient)
                     (. ctx fillRect 10 0 100 300)))
    :onMouseDown (fn [e]
                   (let [x (. e -clientX) y (. e -clientY)
                         ctx (.getContext @_canvas "2d")]
                     (doto ctx
                       (.beginPath)
                       (.moveTo 0 0)
                       (.lineTo x y)
                       (.stroke))))
    :style {:background-color "lightblue"}}
   [:canvas {:ref #(reset! _canvas %)
             :style {:background-color "yellow"}}]
   [:br]
   coor
   ])

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    [:div
     [canvaz]
     [:h1
      "Hello from " @name]
     [counter]
     [mirror-text]
     [:div  {:style {:color "red"}} "green!" [:b "red?"]]
     [slider 20 80]
     [uili (range 3)]
     [:a {:href "http://google.com"} "google.com"]
     [condition-color]
     [slide-color]
     \u2776
     ]))
