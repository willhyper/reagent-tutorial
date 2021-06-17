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
  [:div
   [:input {:type "range" :min m :max M
                 :style {:width "70%"}
            :value (:m @s)
                 :on-change (fn [e]
                              (let [sv (.. e -target -value)
                                    _M (:M @s)]
                           (swap! s assoc :m sv :M (max _M sv))))}]
   [:br]
   [:input {:type "range" :min m :max M
            :style {:width "70%"}
            :value (:M @s)
            :on-change (fn [e]
                         (let [sv (.. e -target -value)
                               _m (:m @s)]
                           (swap! s assoc :M sv :m (min _m sv))))}]
   @s])

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    [:div
     [:h1
      "Hello from " @name]
     [counter]
     [mirror-text]
     [:div  {:style {:color "red"}} "green!" [:b "red?"]]
     [slider 20 80]
     ]))
