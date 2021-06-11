(ns cljs-tutorial.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :refer [atom]]
   [cljs-tutorial.subs :as subs]
   ))

(def cnt (atom 0))

(defn counter []
  [:div [:h1 ">>> " @(re-frame/subscribe [:view-key :cnt])]
   [:input {:type "button"
            :value "inc"
            :on-click #(swap! cnt inc)}]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     [counter]
     ]))
