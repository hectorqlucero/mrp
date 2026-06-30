(ns mrp.handlers.mrp-explosion.view
  (:require
   [mrp.i18n.core :as i18n]
   [mrp.web.csrf :refer [csrf-field]]))

(defn explosion-form
  [request title pedidos]
  [:div.container-fluid.p-4
   [:h2.mb-4 title]
   [:form {:method "POST" :action "/mrp/explosion"}
    (csrf-field)
    [:div.row.mb-3
     [:div.col-md-6
      [:label.form-label.fw-semibold {:for "pedido_id"}
       (i18n/tr :mrp/select_pedido)]
      [:select.form-select {:id "pedido_id" :name "pedido_id" :required true}
       [:option {:value ""} (str "-- " (i18n/tr :common/select) " --")]
       (for [p pedidos]
         [:option {:value (:id p)}
          (str (:codigo p) " - " (:cliente_nombre p) " (" (:fecha p) ")")])]]]
    [:button.btn.btn-primary {:type "submit"}
     [:i.bi.bi-cpu.me-2]
     (i18n/tr :mrp/ejecutar)]]])

(defn explosion-results
  [request title resultados pedido-codigo]
  [:div.container-fluid.p-4
   [:h2.mb-4 (str title " - " pedido-codigo)]

   (if (seq resultados)
     [:div.table-responsive
      [:table.table.table-striped.table-hover
       [:thead.table-dark
        [:tr
         [:th (i18n/tr :mrp/producto)]
         [:th (i18n/tr :mrp/cantidad_pedido)]
         [:th (i18n/tr :mrp/material_requerido)]
         [:th (i18n/tr :mrp/cantidad_requerida)]
         [:th (i18n/tr :mrp/stock_actual_label)]
         [:th (i18n/tr :mrp/deficit)]
         [:th (i18n/tr :common/actions)]]]
       [:tbody
        (for [r resultados]
          [:tr
           (if (:error r)
             [:td {:colspan 7} [:span.text-danger (str (:producto_codigo r) " - " (:producto_nombre r) ": " (:error r))]]
             [:<> [:td (str (:producto_codigo r) " - " (:producto_nombre r))]
              [:td (:pedido_cantidad r)]
              [:td (str (:material_codigo r) " - " (:material_nombre r))]
              [:td (format "%.2f %s" (:cantidad_requerida r) (:unidad_medida r))]
              [:td (format "%.2f" (:stock_actual r))]
              [:td
               (if (pos? (:deficit r))
                 [:span.text-danger.fw-bold (format "%.2f" (:deficit r))]
                 [:span.text-success (i18n/tr :common/ok)])]
              [:td
               (when (pos? (:deficit r))
                 [:a.btn.btn-sm.btn-outline-primary
                  {:href (str "/admin/oc_lineas/add-form?material_id=" (:material_id r)
                              "&cantidad=" (Math/ceil (:deficit r)))
                   :target "_blank"}
                  [:i.bi.bi-cart-plus.me-1]
                  (i18n/tr :mrp/sugerencia_compra)])]])])]]]
     [:div.alert.alert-info (i18n/tr :grid/no-records)])

   [:div.mt-3
    [:a.btn.btn-secondary {:href "/mrp/explosion"}
     [:i.bi.bi-arrow-left.me-2]
     (i18n/tr :common/back)]]])
