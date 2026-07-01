(ns mrp.hooks.productos
  (:require
   [mrp.models.crud :as crud]
   [mrp.engine.crud :as engine]))

(defn before-delete
  [{:keys [id]}]
  (when id
    (doseq [bom (crud/Query ["SELECT id FROM bom WHERE producto_id = ?" id])]
      (engine/delete-record :bom (:id bom)))
    (doseq [op (crud/Query ["SELECT id FROM ordenes_produccion WHERE producto_id = ?" id])]
      (engine/delete-record :ordenes_produccion (:id op))))
  {:success true})
