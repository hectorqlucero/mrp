(ns mrp.routes.proutes
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [mrp.handlers.dashboard.controller :as dashboard]
   [mrp.handlers.reports.controller :as reports]
   [mrp.handlers.mrp-explosion.controller :as mrp-explosion]))

;; All CRUD routes now handled by parameter-driven engine
;; Add custom non-CRUD routes here if needed

(defroutes proutes
  ;; Dashboard
  (GET "/dashboard" req (dashboard/main req))
  ;; System reports
  (GET "/reports/users" req (reports/users req))
  (GET "/reports/audit-log" req (reports/audit-log req))
  ;; MRP Reports
  (GET "/reports/inventory" req (reports/inventory req))
  (GET "/reports/bom-cost" req (reports/bom-cost req))
  (GET "/reports/purchase-status" req (reports/purchase-status req))
  ;; MRP Explosion
  (GET "/mrp/explosion" req (mrp-explosion/main req))
  (POST "/mrp/explosion" req (mrp-explosion/explode req)))
