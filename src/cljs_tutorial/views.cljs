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

(def text-val (atom ""))

(defn mirror-text []
  [:div @text-val
   [:input {:type "text"
                 :value @text-val
                 :on-change #(reset! text-val (-> % .-target .-value))}]]
  )

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     [counter]
     [mirror-text]
     ]))
