(ns mrp.handlers.mrp-explosion.controller
  (:require
   [clojure.string :as str]
   [mrp.handlers.mrp-explosion.model :as model]
   [mrp.handlers.mrp-explosion.view :as view]
   [mrp.layout :refer [application]]
   [mrp.i18n.core :as i18n]
   [mrp.models.util :refer [get-session-id]]))

(defn main
  [request]
  (let [title (i18n/tr :mrp/explosion_title)
        ok (get-session-id request)
        js nil
        pedidos (model/get-pedidos)
        content (view/explosion-form request title pedidos)]
    (application request title ok js content)))

(defn explode
  [request]
  (let [title (i18n/tr :mrp/explosion_title)
        ok (get-session-id request)
        js nil
        pedido-id-str (get-in request [:params :pedido_id])
        pedido-id (when (and pedido-id-str (not (str/blank? pedido-id-str)))
                    (Integer/parseInt pedido-id-str))
        resultados (when pedido-id (model/explode-pedido pedido-id))
        pedido-info (when pedido-id
                      (first (model/get-pedido-by-id pedido-id)))
        pedido-codigo (or (:codigo pedido-info) "")
        content (view/explosion-results request title resultados pedido-codigo)]
    (application request title ok js content)))
