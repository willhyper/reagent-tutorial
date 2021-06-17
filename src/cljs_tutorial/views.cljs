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

(def rgb (atom {:r 0 :g 0 :b 0}))

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
   (let [r (:r @rgb) g (:g @rgb) b (:b @rgb)
         [rh gh bh] (map  #(. % toString 16) [r g b])
         color (str "#" rh gh bh)]
     [:h1 {:style {:color color}} color])
   ]
  )
(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    [:div
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
     ]))
