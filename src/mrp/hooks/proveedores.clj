(ns mrp.hooks.proveedores
  (:require
   [mrp.models.crud :as crud]
   [mrp.engine.crud :as engine]))

(defn before-delete
  [{:keys [id]}]
  (when id
    (doseq [oc (crud/Query ["SELECT id FROM ordenes_compra WHERE proveedor_id = ?" id])]
      (engine/delete-record :ordenes_compra (:id oc))))
  {:success true})
