(ns mrp.handlers.home.view
  (:require
   [mrp.i18n.core :as i18n]
   [mrp.models.form :refer [login-form password-form]]
   [mrp.models.util :refer [user-email user-level]]
   [mrp.web.csrf :refer [csrf-field]]))

(defn home-view
  []
  (list
    [:div.container.mt-4

     ;; === WELCOME ===
     [:div.text-center.mb-5
      [:h1.text-info.fw-bold (i18n/tr :home/welcome_title)]
      [:p.lead.text-muted.mt-2 (i18n/tr :home/welcome_desc)]]

     ;; === DEMO BANNER ===
     [:div.alert.alert-info.d-flex.align-items-center
      [:i.bi.bi-info-circle.me-2.fs-4]
      [:div (i18n/tr :home/seeded_data)]]

     ;; === QUICK ACTIONS ===
     [:h3.mb-3 (i18n/tr :home/quick_actions)]
     [:div.row.g-2.mb-4
      [:div.col-md-4
       [:a.btn.btn-primary.btn-lg.w-100 {:href "/dashboard"}
        [:i.bi.bi-speedometer2.me-2] (i18n/tr :nav/dashboard)]]
      [:div.col-md-4
       [:a.btn.btn-success.btn-lg.w-100 {:href "/mrp/explosion"}
        [:i.bi.bi-lightning.me-2] (i18n/tr :mrp/explosion)]]
      [:div.col-md-4
       [:a.btn.btn-outline-primary.btn-lg.w-100 {:href "/admin/pedidos/new"}
        [:i.bi.bi-plus-circle.me-2] (i18n/tr :home/new_order)]]]

     ;; === MODULE CARDS (clickable) ===
     [:h3.mb-3 (i18n/tr :entity/all)]
     [:div.row.g-3.mb-5
      (for [[icon key path] [["bi-box-seam" :home/module_materials "/admin/materiales"]
                              ["bi-cpu" :home/module_products "/admin/productos"]
                              ["bi-list-check" :home/module_bom "/admin/bom"]
                              ["bi-truck" :home/module_suppliers "/admin/proveedores"]
                              ["bi-cart" :home/module_purchases "/admin/ordenes_compra"]
                              ["bi-people" :home/module_customers "/admin/clientes"]
                              ["bi-file-text" :home/module_orders "/admin/pedidos"]
                              ["bi-gear" :home/module_production "/admin/ordenes_produccion"]
                              ["bi-lightning" :home/module_explosion "/mrp/explosion"]]]
        [:div.col-md-4
         [:a.text-decoration-none.text-reset {:href path}
          [:div.card.h-100.shadow-sm
           [:div.card-body
            [:div.d-flex.align-items-center.mb-2
             [:i.bi.me-2 {:class icon}]
             [:h5.card-title.mb-0 (i18n/tr key)]]
            [:p.card-text.text-muted (i18n/tr (keyword (str (name key) "_desc")))]]]]])]

     ;; === MRP WORKFLOW ===
     [:h3.mb-3 (i18n/tr :home/workflow_title)]
     [:div.card.shadow-sm.mb-4
      [:div.card-body.text-center
       [:p.text-muted (i18n/tr :home/workflow_desc)]
       [:div.d-flex.flex-wrap.justify-content-center.gap-2.mt-3
        (for [[icon label path] [["bi-box-seam" :entity/materiales "/admin/materiales"]
                                  ["bi-list-check" :entity/bom "/admin/bom"]
                                  ["bi-truck" :entity/proveedores "/admin/proveedores"]
                                  ["bi-cart" :entity/ordenes_compra "/admin/ordenes_compra"]
                                  ["bi-people" :entity/clientes "/admin/clientes"]
                                  ["bi-file-text" :entity/pedidos "/admin/pedidos"]
                                  ["bi-lightning" :mrp/explosion "/mrp/explosion"]
                                  ["bi-gear" :entity/ordenes_produccion "/admin/ordenes_produccion"]]]
          [:a.btn.btn-outline-secondary.btn-sm.d-inline-flex.align-items-center.gap-1
           {:href path}
            [:i.bi {:class icon}]
           (i18n/tr label)])]]]

     ;; === TUTORIAL ===
     [:h3.mb-3 (i18n/tr :home/tutorial_title)]
     [:div.card.shadow-sm.mb-4
      [:div.card-body
       [:ol.pe-3
        [:li.mb-2 (i18n/tr :home/tutorial_step1)]
        [:li.mb-2 (i18n/tr :home/tutorial_step2)]
        [:li.mb-2 (i18n/tr :home/tutorial_step3)]
        [:li.mb-2 (i18n/tr :home/tutorial_step4)]
        [:li.mb-2 (i18n/tr :home/tutorial_step5)]
        [:li.mb-2 (i18n/tr :home/tutorial_step6)]
        [:li.mb-2 (i18n/tr :home/tutorial_step7)]
        [:li.mb-2 (i18n/tr :home/tutorial_step8)]]]]]))

(defn main-view
  "This creates the login form and we are passing the title from the controller"
  [title]
  (let [href "/home/login"]
    (login-form title href)))

(defn temp-password-view
  [users selected-username message temp-password]
  [:div.container.mt-5
   [:div.row.justify-content-center
    [:div.col-lg-8
     [:div.card.shadow
      [:div.card-header.bg-primary.text-white
       [:h4.mb-0 (i18n/tr :temp-password/title)]]
      [:div.card-body
       (when message
         [:div.alert.alert-info message])
       [:form {:method "POST" :action "/home/temp-password"}
        (csrf-field)
        [:div.mb-3
         [:label.form-label.fw-semibold {:for "username"}
          (i18n/tr :temp-password/select-user)]
         [:select.form-select {:id "username" :name "username" :required true}
          [:option {:value ""} (i18n/tr :temp-password/placeholder-user)]
          (for [user users]
            [:option {:value (:username user)
                      :selected (= (:username user) selected-username)}
             (str (:username user)
                  (when-let [email (:email user)]
                    (str " (" email ")")))])]]
        [:div.d-flex.gap-2.justify-content-end.mt-4
         [:button.btn.btn-success {:type "submit"}
          (i18n/tr :temp-password/title)]]]
       (when temp-password
         [:div.mt-4
          [:div.alert.alert-success
           [:h5.mb-0 (i18n/tr :temp-password/created)]]
          [:p.mb-1 (i18n/tr :temp-password/copy-warning)]
          [:pre.p-3.bg-light.rounded [:code temp-password]]])]]]]])

(defn forgot-password-view
  [email message]
  [:div.container.d-flex.justify-content-center.align-items-center
   {:style "min-height: 80vh;"}
   [:div.card.shadow-lg.w-100
    {:style "max-width: 420px;"}
    [:div.card-header.bg-primary.text-white.text-center
     [:h4.mb-0.fw-bold (i18n/tr :auth/reset-password)]]
    [:div.card-body.p-4
     (when message
       [:div.alert.alert-info message])
     [:form {:method "POST" :action "/home/forgot-password"}
      (csrf-field)
      [:div.mb-3
       [:label.form-label.fw-semibold {:for "email"}
        [:i.bi.bi-envelope.me-2] (i18n/tr :form/email)]
       [:input.form-control.form-control-lg
        {:id "email"
         :name "email"
         :type "email"
         :required true
         :placeholder (i18n/tr :form/email)
         :autocomplete "email"}]]
      [:div.d-flex.gap-2.justify-content-end.mt-4
       [:button.btn.btn-success.btn-lg.fw-semibold
        {:type "submit"}
        [:i.bi.bi-send.me-2] (i18n/tr :auth/reset-password)]]]
     [:div.text-center.mt-3
      [:a.small.text-decoration-none {:href "/home/login"}
       (i18n/tr :common/back) " " (i18n/tr :auth/login)]]]]])

(defn change-password-view
  [request title]
  (let [level (user-level request)
        email (user-email request)
        email-readonly? (not (some #(= level %) #{"A" "S"}))]
    (password-form title :user-email email :email-readonly? email-readonly?)))
