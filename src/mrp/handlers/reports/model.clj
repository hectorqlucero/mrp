(ns mrp.handlers.reports.model
  (:require
   [mrp.models.crud :refer [Query]]))

(def ^:private users-sql
  "select * from users_view")

(defn get-users
  []
  (Query users-sql))

(def ^:private audit-log-sql
  "select a.*, u.username as user_name
   from audit_log a
   left join users u on a.user_id = u.id
   order by a.timestamp desc")

(defn get-audit-log
  []
  (Query audit-log-sql))

;; ---------------------------------------------------------------------------
;; MRP Reports
;; ---------------------------------------------------------------------------

(defn get-inventory
  []
  (Query ["SELECT m.codigo, m.nombre,
                  CASE m.tipo
                    WHEN 'madera'  THEN 'Madera'
                    WHEN 'tela'    THEN 'Tela'
                    WHEN 'espuma'  THEN 'Espuma'
                    WHEN 'herraje' THEN 'Herraje'
                    ELSE 'Otro'
                  END AS tipo,
                  m.unidad_medida, m.stock_actual, m.stock_minimo,
                  (m.stock_minimo - m.stock_actual) AS deficit,
                  CASE WHEN m.stock_actual < m.stock_minimo THEN 'Bajo' ELSE 'OK' END AS estado_stock,
                  m.ubicacion, m.costo_unitario,
                  ROUND(m.stock_actual * m.costo_unitario, 2) AS valor_inventario
           FROM materiales m
           WHERE m.activo = 'T'
           ORDER BY estado_stock, m.nombre"]))

(defn get-bom-cost
  []
  (Query ["SELECT p.codigo AS producto_codigo, p.nombre AS producto_nombre,
                  b.codigo AS bom_codigo,
                  COUNT(bl.id) AS num_materiales,
                  ROUND(SUM(bl.cantidad * m.costo_unitario), 2) AS costo_materiales,
                  p.precio_venta,
                  ROUND(p.precio_venta - SUM(bl.cantidad * m.costo_unitario), 2) AS margen
           FROM productos p
           JOIN bom b ON b.producto_id = p.id
           JOIN bom_lineas bl ON bl.bom_id = b.id
           JOIN materiales m ON bl.material_id = m.id
           WHERE p.activo = 'T'
           GROUP BY p.id
           ORDER BY p.nombre"]))

(defn get-purchase-status
  []
  (Query ["SELECT oc.codigo, pr.nombre AS proveedor, oc.fecha, oc.fecha_entrega,
                  oc.estado,
                  COUNT(ocl.id) AS lineas,
                  ROUND(SUM(ocl.cantidad * ocl.precio_unitario), 2) AS total_oc
           FROM ordenes_compra oc
           JOIN proveedores pr ON oc.proveedor_id = pr.id
           LEFT JOIN oc_lineas ocl ON ocl.oc_id = oc.id
           GROUP BY oc.id
           ORDER BY oc.fecha DESC"]))
