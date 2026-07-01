(ns mrp.hooks.oc-lineas
  (:require
   [mrp.models.crud :as crud]))

(defn- ->num
  [v]
  (try
    (if (number? v) v (Double/parseDouble (str v)))
    (catch Exception _ 0)))

(defn- parent-is-recibida?
  [oc-id]
  (when-let [oc (first (crud/Query ["SELECT estado FROM ordenes_compra WHERE id = ?" oc-id]))]
    (= "recibida" (:estado oc))))

(defn before-save
  [data]
  (if (:id data)
    (let [old (first (crud/Query ["SELECT cantidad FROM oc_lineas WHERE id = ?" (:id data)]))
          old-cant (->num (:cantidad old))
          new-cant (->num (:cantidad data))]
      (assoc data :_stock-delta (- new-cant old-cant)
                  :_material-id (->num (:material_id data))
                  :_oc-id (->num (:oc_id data))))
    (assoc data :_stock-delta (->num (:cantidad data))
                :_material-id (->num (:material_id data))
                :_oc-id (->num (:oc_id data)))))

(defn after-save
  [data _result]
  (let [delta (:_stock-delta data)]
    (when (and (not= delta 0) (parent-is-recibida? (:_oc-id data)))
      (crud/Query! ["UPDATE materiales SET stock_actual = COALESCE(stock_actual, 0) + ? WHERE id = ?"
                    delta (:_material-id data)])))
  {:success true})

(defn before-delete
  [{:keys [id]}]
  (when id
    (let [line (first (crud/Query ["SELECT oc_id, material_id, cantidad FROM oc_lineas WHERE id = ?" id]))]
      (when (parent-is-recibida? (:oc_id line))
        (crud/Query! ["UPDATE materiales SET stock_actual = COALESCE(stock_actual, 0) - ? WHERE id = ?"
                      (:cantidad line) (:material_id line)]))))
  {:success true})
