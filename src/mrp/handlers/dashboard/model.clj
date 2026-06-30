(ns mrp.handlers.dashboard.model
  (:require
   [mrp.models.crud :refer [Query]]))

(defn materials-low-stock
  []
  (Query ["SELECT m.id, m.codigo, m.nombre, m.stock_actual, m.stock_minimo,
                  m.unidad_medida,
                  (m.stock_minimo - m.stock_actual) AS deficit,
                  (SELECT GROUP_CONCAT(op.codigo, ', ')
                   FROM ordenes_produccion op
                   JOIN productos pr ON op.producto_id = pr.id
                   JOIN bom b ON b.producto_id = pr.id
                   JOIN bom_lineas bl ON bl.bom_id = b.id
                   WHERE bl.material_id = m.id
                     AND op.estado IN ('planificada', 'en_proceso')
                  ) AS ops_afectadas
           FROM materiales m
           WHERE m.stock_actual < m.stock_minimo AND m.activo = 'T'
           ORDER BY deficit DESC"]))

(defn open-purchase-orders
  []
  (let [rows (Query ["SELECT o.id, o.codigo, p.nombre AS proveedor, o.fecha, o.fecha_entrega, o.estado
                      FROM ordenes_compra o
                      LEFT JOIN proveedores p ON o.proveedor_id = p.id
                      WHERE o.estado IN ('pendiente', 'aprobada')
                      ORDER BY o.fecha DESC"])]
    (map #(assoc % :edit-path "ordenes_compra") rows)))

(defn open-sales-orders
  []
  (let [rows (Query ["SELECT o.id, o.codigo, c.nombre AS cliente, o.fecha, o.fecha_entrega, o.estado
                      FROM pedidos o
                      LEFT JOIN clientes c ON o.cliente_id = c.id
                      WHERE o.estado IN ('pendiente', 'confirmado')
                      ORDER BY o.fecha DESC"])]
    (map #(assoc % :edit-path "pedidos") rows)))

(defn production-orders-pending
  []
  (let [rows (Query ["SELECT op.id, op.codigo, pr.nombre AS producto, op.cantidad,
                             op.fecha_inicio, op.estado
                      FROM ordenes_produccion op
                      LEFT JOIN productos pr ON op.producto_id = pr.id
                      WHERE op.estado IN ('planificada', 'en_proceso')
                      ORDER BY op.fecha_inicio DESC"])]
    (map #(assoc % :edit-path "ordenes_produccion") rows)))

(defn completed-production-orders
  []
  (Query ["SELECT op.id, op.codigo, pr.nombre AS producto, op.cantidad, op.fecha_fin, op.estado
           FROM ordenes_produccion op
           LEFT JOIN productos pr ON op.producto_id = pr.id
           WHERE op.estado = 'completada'
           ORDER BY op.fecha_fin DESC"]))

(defn get-stats
  []
  (let [mat-count     (-> (Query ["SELECT COUNT(*) AS c FROM materiales"]) first :c)
        prod-count    (-> (Query ["SELECT COUNT(*) AS c FROM productos"]) first :c)
        bom-count     (-> (Query ["SELECT COUNT(*) AS c FROM bom"]) first :c)
        prov-count    (-> (Query ["SELECT COUNT(*) AS c FROM proveedores"]) first :c)
        cli-count     (-> (Query ["SELECT COUNT(*) AS c FROM clientes"]) first :c)
        ped-count     (-> (Query ["SELECT COUNT(*) AS c FROM pedidos"]) first :c)
        oc-count      (-> (Query ["SELECT COUNT(*) AS c FROM ordenes_compra"]) first :c)
        op-count      (-> (Query ["SELECT COUNT(*) AS c FROM ordenes_produccion"]) first :c)
        bajo-stock    (materials-low-stock)
        oc-abiertas   (open-purchase-orders)
        ped-abiertos  (open-sales-orders)
        op-pend       (production-orders-pending)
        op-complet    (completed-production-orders)]
    {:materiales              mat-count
     :productos               prod-count
     :bom                     bom-count
     :proveedores              prov-count
     :clientes                cli-count
     :pedidos                 ped-count
     :ordenes_compra          oc-count
     :ordenes_produccion       op-count
     :bajo_stock              (count bajo-stock)
     :pendientes_compra        (->> oc-abiertas (filter #(= (:estado %) "pendiente")) count)
     :aprobadas_compra         (->> oc-abiertas (filter #(= (:estado %) "aprobada")) count)
     :pendientes_pedidos       (->> ped-abiertos (filter #(= (:estado %) "pendiente")) count)
     :confirmados_pedidos      (->> ped-abiertos (filter #(= (:estado %) "confirmado")) count)
     :produccion_planificada   (->> op-pend (filter #(= (:estado %) "planificada")) count)
     :produccion_en_proceso    (->> op-pend (filter #(= (:estado %) "en_proceso")) count)
     :materiales-bajo-stock    bajo-stock
     :oc-abiertas              oc-abiertas
     :pedidos-abiertos         ped-abiertos
     :op-pendientes            op-pend
     :op-completadas           op-complet}))
