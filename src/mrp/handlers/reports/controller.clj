(ns mrp.handlers.reports.controller
  (:require
   [mrp.handlers.reports.model :as model]
   [mrp.handlers.reports.view :as view]
   [mrp.layout :refer [application]]
   [mrp.models.util :refer [get-session-id]]))

(defn users
  [request]
  (let [title "Reporte de usuarios"
        ok (get-session-id request)
        js nil
        rows (model/get-users)
        content (view/users request title rows)]
    (application request title ok js content)))

(defn audit-log
  [request]
  (let [title "Reporte de auditoría"
        ok (get-session-id request)
        js nil
        rows (model/get-audit-log)
        content (view/audit-log request title rows)]
    (application request title ok js content)))

;; ---------------------------------------------------------------------------
;; MRP Reports
;; ---------------------------------------------------------------------------

(defn inventory
  [request]
  (let [title "Inventario de Materiales"
        ok (get-session-id request)
        js nil
        rows (model/get-inventory)
        content (view/inventory request title rows)]
    (application request title ok js content)))

(defn bom-cost
  [request]
  (let [title "Análisis de Costos BOM"
        ok (get-session-id request)
        js nil
        rows (model/get-bom-cost)
        content (view/bom-cost request title rows)]
    (application request title ok js content)))

(defn purchase-status
  [request]
  (let [title "Estado de Órdenes de Compra"
        ok (get-session-id request)
        js nil
        rows (model/get-purchase-status)
        content (view/purchase-status request title rows)]
    (application request title ok js content)))
