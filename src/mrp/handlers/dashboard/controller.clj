(ns mrp.handlers.dashboard.controller
  (:require
   [mrp.handlers.dashboard.model :as model]
   [mrp.handlers.dashboard.view :as view]
   [mrp.i18n.core :as i18n]
   [mrp.layout :refer [application]]
   [mrp.models.util :refer [get-session-id]]))

(defn main
  [request]
  (let [title (i18n/tr :layout/home)
        ok (get-session-id request)
        js nil
        stats (model/get-stats)
        content (view/main title stats)]
    (application request title ok js content)))
