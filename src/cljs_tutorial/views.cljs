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

(def s (atom 0))

(defn slider [m M]
  [:div [:input {:type "range" :min m :max M
                 :value @s
                 :on-change (fn [e]
                              (let [sv (.. e -target -value)]
                                (swap! s (constantly sv))))}]
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
