(ns cljs-tutorial.events
  (:require
   [re-frame.core :as re-frame]
   [cljs-tutorial.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 :cnt-event
 (fn [db [event-id handler]]
   (assoc db :cnt (handler (:cnt db)))))