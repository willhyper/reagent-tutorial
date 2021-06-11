(ns cljs-tutorial.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :view-key
 (fn [db [event-id key]]
   (key db)))