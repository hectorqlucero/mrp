(ns mrp.handlers.mrp-explosion.model
  (:require
   [mrp.models.crud :refer [Query]]))

(def ^:private pedidos-sql
  "SELECT p.id, p.codigo, c.codigo || ' - ' || c.nombre AS cliente_nombre,
          p.fecha, p.estado
   FROM pedidos p
   LEFT JOIN clientes c ON p.cliente_id = c.id
   WHERE p.estado != 'cancelado'
   ORDER BY p.id DESC")

(defn get-pedidos
  []
  (Query pedidos-sql))

(defn get-pedido-by-id
  [pedido-id]
  (Query ["SELECT p.*, c.codigo || ' - ' || c.nombre AS cliente_nombre
           FROM pedidos p
           LEFT JOIN clientes c ON p.cliente_id = c.id
           WHERE p.id = ?"
          pedido-id]))

(defn get-pedido-lineas
  [pedido-id]
  (Query ["SELECT pl.*, pr.codigo AS producto_codigo, pr.nombre AS producto_nombre
           FROM pedido_lineas pl
           LEFT JOIN productos pr ON pl.producto_id = pr.id
           WHERE pl.pedido_id = ?"
          pedido-id]))

(defn get-producto-bom
  [producto-id]
  (Query ["SELECT b.id, b.codigo, b.descripcion
           FROM bom b
           WHERE b.producto_id = ? AND b.activo = 'T'
           ORDER BY b.id DESC
           LIMIT 1"
          producto-id]))

(defn get-bom-lineas
  [bom-id]
  (Query ["SELECT bl.*, m.codigo AS material_codigo, m.nombre AS material_nombre,
                  m.unidad_medida, m.stock_actual, m.stock_minimo, m.costo_unitario
           FROM bom_lineas bl
           LEFT JOIN materiales m ON bl.material_id = m.id
           WHERE bl.bom_id = ?"
          bom-id]))

(defn explode-pedido
  [pedido-id]
  (let [lineas (get-pedido-lineas pedido-id)
        resultados (atom [])]
    (doseq [linea lineas]
      (let [pedido-cantidad (:cantidad linea)
            producto-id (:producto_id linea)
            bom-results (get-producto-bom producto-id)
            bom (first bom-results)]
        (if bom
          (let [bom-lineas (get-bom-lineas (:id bom))]
            (doseq [bl bom-lineas]
              (let [cantidad-por-unidad (or (:cantidad bl) 0)
                    desperdicio (or (:desperdicio_porcentaje bl) 0)
                    cantidad-bruta (* pedido-cantidad cantidad-por-unidad)
                    cantidad-con-desperdicio (* cantidad-bruta (+ 1 (/ desperdicio 100)))
                    stock-actual (or (:stock_actual bl) 0)
                    deficit (max 0 (- cantidad-con-desperdicio stock-actual))]
                (swap! resultados conj
                       {:pedido_id pedido-id
                        :producto_id producto-id
                        :producto_codigo (:producto_codigo linea)
                        :producto_nombre (:producto_nombre linea)
                        :pedido_cantidad pedido-cantidad
                        :material_id (:material_id bl)
                        :material_codigo (:material_codigo bl)
                        :material_nombre (:material_nombre bl)
                        :unidad_medida (:unidad_medida bl)
                        :cantidad_por_unidad cantidad-por-unidad
                        :desperdicio_porcentaje desperdicio
                        :cantidad_requerida cantidad-con-desperdicio
                        :stock_actual stock-actual
                        :stock_minimo (or (:stock_minimo bl) 0)
                        :deficit deficit
                        :costo_unitario (or (:costo_unitario bl) 0)
                        :costo_total (* deficit (or (:costo_unitario bl) 0))}))))
          (swap! resultados conj
                 {:producto_codigo (:producto_codigo linea)
                  :producto_nombre (:producto_nombre linea)
                  :pedido_cantidad pedido-cantidad
                  :error "Sin BOM activa"}))))
    @resultados))
