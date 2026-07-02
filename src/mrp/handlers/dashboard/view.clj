(ns mrp.handlers.dashboard.view
  (:require
   [mrp.i18n.core :as i18n]))

(defn- kpi-card
  [label value icon variant]
  [:div.col-md-3.col-sm-6.mb-3
   [:div.card.border-0.shadow-sm.h-100
    [:div.card-body
     [:div.d-flex.justify-content-between.align-items-center
      [:div
       [:h6.card-subtitle.text-muted.mb-1 label]
       [:h2.mb-0 (str value)]]
      [:div
       [:i.bi {:class (str icon " fs-1 text-" variant)}]]]]]])

(defn- status-badge
  [estado]
  (let [variant (case estado
                  "pendiente" "warning"
                  "aprobada" "info"
                  "recibida" "success"
                  "confirmado" "primary"
                  "enviado" "info"
                  "entregado" "success"
                  "planificada" "secondary"
                  "en_proceso" "warning"
                  "completada" "success"
                  "cancelada" "danger"
                  "cancelado" "danger"
                  "secondary")]
    [:span.badge {:class (str "bg-" variant)} (i18n/tr (keyword (str "status/" estado)))]))

(defn- table-section
  [title items cols row-fn empty-key edit-path]
  (let [header (into [:tr] (map (fn [c] [:th c]) cols))
        rows (map row-fn items)
        tbody [:tbody rows]
        thead [:thead.table-light header]
        table [:table.table.table-hover.mb-0 thead tbody]
        content [:div.table-responsive table]]
    [:div.card.border-0.shadow-sm.mb-4
     [:div.card-header.bg-white.d-flex.justify-content-between.align-items-center
      [:h5.mb-0 title]
      [:span.badge.bg-primary (count items)]]
     (if (seq items)
       content
       [:div.card-body
        [:p.text-muted.mb-0 (i18n/tr empty-key)]])]))

(defn- so-row
  [o]
  [:tr
   [:td [:a {:href (str "/admin/pedidos/" (:id o))} (:codigo o)]]
   [:td (:cliente o)]
   [:td (:fecha o)]
   [:td (status-badge (:estado o))]])

(defn- po-row
  [o]
  [:tr
   [:td [:a {:href (str "/admin/ordenes_compra/" (:id o))} (:codigo o)]]
   [:td (:proveedor o)]
   [:td (:fecha o)]
   [:td (status-badge (:estado o))]])

(defn- alert-row
  [m]
  [:tr
   [:td [:a {:href (str "/admin/materiales/" (:id m))} (:codigo m)]]
   [:td (:nombre m)]
   [:td (:stock_actual m)]
   [:td (:stock_minimo m)]
   [:td.text-danger.fw-bold (format "%.0f" (:deficit m))]
   [:td (when-let [ops (:ops_afectadas m)] (when (not= ops "") ops))]
   [:td [:a.btn.btn-sm.btn-outline-danger {:href "/admin/ordenes_compra/new"} (i18n/tr :dashboard/order)]]])

(defn- prod-row
  [op]
  [:tr
   [:td [:a {:href (str "/admin/ordenes_produccion/" (:id op))} (:codigo op)]]
   [:td (:producto op)]
   [:td (:cantidad op)]
   [:td (status-badge (:estado op))]])

(defn- sales-orders-card
  [items]
  (table-section (i18n/tr :dashboard/open_orders) items
                 [(i18n/tr :pedidos/codigo) (i18n/tr :clientes/nombre)
                  (i18n/tr :pedidos/fecha) (i18n/tr :pedidos/estado)]
                 so-row :dashboard/no_open_orders "pedidos"))

(defn- purchase-orders-card
  [items]
  (table-section (i18n/tr :dashboard/open_purchases) items
                 [(i18n/tr :ordenes_compra/codigo) (i18n/tr :proveedores/nombre)
                  (i18n/tr :ordenes_compra/fecha) (i18n/tr :ordenes_compra/estado)]
                 po-row :dashboard/no_open_purchases "ordenes_compra"))

(defn- materials-alert-card
  [items]
  (let [rows (map alert-row items)
        header [:tr
                [:th (i18n/tr :materiales/codigo)]
                [:th (i18n/tr :materiales/nombre)]
                [:th (i18n/tr :materiales/stock_actual)]
                [:th (i18n/tr :materiales/stock_minimo)]
                [:th (i18n/tr :dashboard/deficit)]
                [:th (i18n/tr :dashboard/affected_ops)]
                [:th (i18n/tr :dashboard/action)]]]
    [:div.card.border-0.shadow-sm.mb-4
     [:div.card-header.bg-white.d-flex.justify-content-between.align-items-center
      [:h5.mb-0 (i18n/tr :dashboard/materials_low_stock)]
      (when (seq items)
        [:span.badge.bg-warning.text-dark (count items)])]
     (if (seq items)
       [:div.table-responsive
        [:table.table.table-hover.mb-0
         [:thead.table-light header]
         [:tbody rows]]]
       [:div.card-body
        [:p.text-muted.mb-0 (i18n/tr :dashboard/no_alerts)]])]))

(defn- production-panel
  [{:keys [op-pendientes produccion_planificada produccion_en_proceso op-completadas] :as stats}]
  (list
   [:div.card.border-0.shadow-sm.mb-4
    [:div.card-header.bg-white.d-flex.justify-content-between.align-items-center
     [:h5.mb-0 (i18n/tr :dashboard/production_status)]
     [:span.badge.bg-success (+ produccion_planificada produccion_en_proceso)]]
    [:div.card-body
     [:div.d-flex.justify-content-around.text-center.mb-3
      [:div [:h3.text-info produccion_planificada] [:small.text-muted (i18n/tr :status/planificada)]]
      [:div [:h3.text-warning produccion_en_proceso] [:small.text-muted (i18n/tr :status/en_proceso)]]
      [:div [:h3.text-success (count op-completadas)] [:small.text-muted (i18n/tr :status/completada)]]]
     (when (seq op-pendientes)
       [:div.table-responsive
        [:table.table.table-hover.mb-0
         [:thead.table-light
          [:tr
           [:th (i18n/tr :ordenes_produccion/codigo)]
           [:th (i18n/tr :productos/nombre)]
           [:th (i18n/tr :ordenes_produccion/cantidad)]
           [:th (i18n/tr :ordenes_produccion/estado)]]]
         [:tbody (map prod-row op-pendientes)]]])
     (when (seq op-completadas)
       [:div.mt-3
        [:h6.text-success.mb-2 (i18n/tr :dashboard/completed_production)]
        [:div.table-responsive
         [:table.table.table-sm.table-hover.mb-0
          [:thead.table-light
           [:tr
            [:th (i18n/tr :ordenes_produccion/codigo)]
            [:th (i18n/tr :productos/nombre)]
            [:th (i18n/tr :ordenes_produccion/cantidad)]
            [:th (i18n/tr :ordenes_produccion/estado)]]]
          [:tbody (map prod-row op-completadas)]]]])]]))

(defn main
  [title stats]
  [:div.container-fluid.p-4
   [:style ".table td { white-space: nowrap; }"]
   [:div.d-flex.justify-content-between.align-items-center.mb-4
    [:h2.mb-0 title]
    [:a.btn.btn-outline-primary {:href "/mrp/explosion"}
     [:i.bi.bi-lightning.me-1] (i18n/tr :mrp/explosion)]]
   [:div.row
    (kpi-card (i18n/tr :entity/materiales) (:materiales stats) "bi-box-seam" "primary")
    (kpi-card (i18n/tr :entity/productos) (:productos stats) "bi-cpu" "success")
    (kpi-card (i18n/tr :entity/bom) (:bom stats) "bi-list-check" "info")
    (kpi-card (i18n/tr :entity/proveedores) (:proveedores stats) "bi-truck" "secondary")
    (kpi-card (i18n/tr :entity/clientes) (:clientes stats) "bi-people" "warning")
    (kpi-card (i18n/tr :entity/ordenes_compra) (:ordenes_compra stats) "bi-cart" "danger")
    (kpi-card (i18n/tr :entity/pedidos) (:pedidos stats) "bi-file-text" "dark")
    (kpi-card (i18n/tr :entity/ordenes_produccion) (:ordenes_produccion stats) "bi-gear" "primary")]
   [:div.row
    [:div.col-lg-6 (sales-orders-card (:pedidos-abiertos stats))]
    [:div.col-lg-6 (purchase-orders-card (:oc-abiertas stats))]]
   [:div.row
    [:div.col-lg-7 (materials-alert-card (:materiales-bajo-stock stats))]
    [:div.col-lg-5 (production-panel stats)]]])
