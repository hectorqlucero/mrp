(ns mrp.hooks.clientes
  (:require
   [mrp.models.crud :as crud]
   [mrp.engine.crud :as engine]))

(defn before-delete
  [{:keys [id]}]
  (when id
    (doseq [ped (crud/Query ["SELECT id FROM pedidos WHERE cliente_id = ?" id])]
      (engine/delete-record :pedidos (:id ped))))
  {:success true})
