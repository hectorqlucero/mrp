(ns mrp.hooks.ordenes-compra
  (:require
   [mrp.models.crud :as crud]))

(defn before-save
  [data]
  (if (= "recibida" (:estado data))
    (assoc data :_update-stock true)
    data))

(defn after-save
  [data _result]
  (when (:_update-stock data)
    (let [oc-id (or (:id data) _result)
          lines (crud/Query ["SELECT material_id, cantidad FROM oc_lineas WHERE oc_id = ?" oc-id])]
      (doseq [line lines]
        (crud/Query! ["UPDATE materiales SET stock_actual = COALESCE(stock_actual, 0) + ? WHERE id = ?"
                      (:cantidad line) (:material_id line)]))))
  {:success true})

(defn before-delete
  [{:keys [id]}]
  (when id
    (let [oc (first (crud/Query ["SELECT estado FROM ordenes_compra WHERE id = ?" id]))]
      (when (and oc (= "recibida" (:estado oc)))
        (let [lines (crud/Query ["SELECT material_id, cantidad FROM oc_lineas WHERE oc_id = ?" id])]
          (doseq [line lines]
            (crud/Query! ["UPDATE materiales SET stock_actual = COALESCE(stock_actual, 0) - ? WHERE id = ?"
                          (:cantidad line) (:material_id line)]))))))
  {:success true})
