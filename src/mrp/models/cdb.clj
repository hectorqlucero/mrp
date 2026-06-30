(ns mrp.models.cdb
  (:require
   [clojure.java.io :as io]
   [clojure.string :as st]
   [buddy.hashers :as hashers]
   [clj-time.core :as t]
   [mrp.models.crud :as crud :refer [Insert-multi Query!]]))

(def users-rows
  [{:lastname  "User"
    :firstname "Regular"
    :username  "user@example.com"
    :password  (hashers/derive "user")
    :dob       "1957-02-07"
    :email     "user@example.com"
    :level     "U"
    :active    "T"}
   {:lastname "User"
    :firstname "Admin"
    :username "admin@example.com"
    :password (hashers/derive "admin")
    :dob "1957-02-07"
    :email "admin@example.com"
    :level "A"
    :active "T"}
   {:lastname "User"
    :firstname "System"
    :username "system@example.com"
    :password (hashers/derive "system")
    :dob "1957-02-07"
    :email "system@example.com"
    :level "S"
    :active "T"}])

(defn ^:private seed-year []
  (str (t/year (t/now))))

(def materiales-rows
  [{:id 1 :codigo "MAD-ROB" :nombre "Madera de Roble" :descripcion "Madera sólida de roble nacional, ideal para estructuras de sillas" :tipo "madera" :unidad_medida "m" :costo_unitario 45.00 :stock_actual 120 :stock_minimo 50 :ubicacion "Almacén A-01" :activo "T"}
   {:id 2 :codigo "MAD-PIN" :nombre "Madera de Pino" :descripcion "Madera de pino tratada, económica y resistente" :tipo "madera" :unidad_medida "m" :costo_unitario 18.50 :stock_actual 200 :stock_minimo 80 :ubicacion "Almacén A-02" :activo "T"}
   {:id 3 :codigo "ESP-POL" :nombre "Espuma de Poliuretano" :descripcion "Espuma de alta densidad para asientos" :tipo "espuma" :unidad_medida "kg" :costo_unitario 32.00 :stock_actual 45 :stock_minimo 30 :ubicacion "Almacén B-01" :activo "T"}
   {:id 4 :codigo "TEL-MIC" :nombre "Tela Microfibra" :descripcion "Tela microfibra resistente al desgaste, color gris" :tipo "tela" :unidad_medida "m2" :costo_unitario 28.00 :stock_actual 80 :stock_minimo 40 :ubicacion "Almacén B-02" :activo "T"}
   {:id 5 :codigo "TEL-CUE" :nombre "Cuero Sintético" :descripcion "Cuero sintético premium para tapicería" :tipo "tela" :unidad_medida "m2" :costo_unitario 55.00 :stock_actual 30 :stock_minimo 20 :ubicacion "Almacén B-02" :activo "T"}
   {:id 6 :codigo "TOR-ACE" :nombre "Tornillos de Acero" :descripcion "Tornillos hexagonales 1/4 x 2 pulgadas" :tipo "herraje" :unidad_medida "pieza" :costo_unitario 0.15 :stock_actual 5000 :stock_minimo 1000 :ubicacion "Almacén C-01" :activo "T"}
   {:id 7 :codigo "TUB-ALU" :nombre "Tubo de Aluminio" :descripcion "Tubo de aluminio ligero de 1 pulgada" :tipo "herraje" :unidad_medida "m" :costo_unitario 12.00 :stock_actual 60 :stock_minimo 40 :ubicacion "Almacén C-02" :activo "T"}
   {:id 8 :codigo "TEL-LON" :nombre "Tela de Lona" :descripcion "Lona resistente para asientos plegables" :tipo "tela" :unidad_medida "m2" :costo_unitario 15.00 :stock_actual 25 :stock_minimo 30 :ubicacion "Almacén B-03" :activo "T"}
   {:id 9 :codigo "BAR-TRA" :nombre "Barniz Transparente" :descripcion "Barniz poliuretánico transparente mate" :tipo "otro" :unidad_medida "litro" :costo_unitario 22.00 :stock_actual 35 :stock_minimo 15 :ubicacion "Almacén D-01" :activo "T"}
   {:id 10 :codigo "PLA-ABS" :nombre "Plástico ABS" :descripcion "Plástico ABS granulado para inyección" :tipo "otro" :unidad_medida "kg" :costo_unitario 8.50 :stock_actual 100 :stock_minimo 50 :ubicacion "Almacén D-02" :activo "T"}
   {:id 11 :codigo "MUE-ACE" :nombre "Muelles de Acero" :descripcion "Muelles helicoidales para asientos" :tipo "herraje" :unidad_medida "pieza" :costo_unitario 3.50 :stock_actual 150 :stock_minimo 60 :ubicacion "Almacén C-01" :activo "T"}
   {:id 12 :codigo "LIJ-MAD" :nombre "Lija de Madera" :descripcion "Lija de grano fino para acabado de madera" :tipo "otro" :unidad_medida "pieza" :costo_unitario 0.80 :stock_actual 400 :stock_minimo 100 :ubicacion "Almacén D-01" :activo "T"}
   {:id 13 :codigo "BAS-GIR" :nombre "Base Giratoria" :descripcion "Base giratoria de nylon reforzado para sillas de oficina" :tipo "herraje" :unidad_medida "pieza" :costo_unitario 25.00 :stock_actual 18 :stock_minimo 15 :ubicacion "Almacén C-03" :activo "T"}
   {:id 14 :codigo "RUE-SIL" :nombre "Ruedas para Silla" :descripcion "Set de 5 ruedas de nylon para sillas de oficina" :tipo "herraje" :unidad_medida "pieza" :costo_unitario 2.00 :stock_actual 40 :stock_minimo 50 :ubicacion "Almacén C-03" :activo "T"}
   {:id 15 :codigo "RES-MAL" :nombre "Respaldo de Malla" :descripcion "Respaldo ergonómico de malla transpirable" :tipo "otro" :unidad_medida "pieza" :costo_unitario 35.00 :stock_actual 12 :stock_minimo 15 :ubicacion "Almacén B-04" :activo "T"}
   {:id 16 :codigo "HIL-IND" :nombre "Hilo de Coser Industrial" :descripcion "Hilo de nylon resistente para tapicería" :tipo "otro" :unidad_medida "pieza" :costo_unitario 3.00 :stock_actual 60 :stock_minimo 20 :ubicacion "Almacén B-02" :activo "T"}])

(def productos-rows
  [{:id 1 :codigo "ERS-001" :nombre "Silla Ejecutiva Ergonómica" :descripcion "Silla de oficina con soporte lumbar ajustable, reposabrazos 3D y base cromada" :precio_venta 3500.00 :activo "T"}
   {:id 2 :codigo "CMD-001" :nombre "Silla de Comedor Clásica" :descripcion "Silla de comedor de madera de roble con asiento tapizado" :precio_venta 1200.00 :activo "T"}
   {:id 3 :codigo "PLG-001" :nombre "Silla Plegable" :descripcion "Silla plegable de aluminio con asiento de lona, fácil de almacenar" :precio_venta 450.00 :activo "T"}
   {:id 4 :codigo "MCD-001" :nombre "Silla Mecedora" :descripcion "Mecedora clásica de madera de roble con acabado barnizado" :precio_venta 2800.00 :activo "T"}
   {:id 5 :codigo "INF-001" :nombre "Silla Infantil" :descripcion "Silla infantil de pino con diseños coloridos, ideal para escuelas" :precio_venta 380.00 :activo "T"}])

(def bom-rows
  [{:id 1 :codigo "BOM-ERS-001" :producto_id 1 :descripcion "Lista de materiales para Silla Ejecutiva Ergonómica" :activo "T"}
   {:id 2 :codigo "BOM-CMD-001" :producto_id 2 :descripcion "Lista de materiales para Silla de Comedor Clásica" :activo "T"}
   {:id 3 :codigo "BOM-PLG-001" :producto_id 3 :descripcion "Lista de materiales para Silla Plegable" :activo "T"}
   {:id 4 :codigo "BOM-MCD-001" :producto_id 4 :descripcion "Lista de materiales para Silla Mecedora" :activo "T"}
   {:id 5 :codigo "BOM-INF-001" :producto_id 5 :descripcion "Lista de materiales para Silla Infantil" :activo "T"}])

(def bom-lineas-rows
  [{:id 1 :bom_id 1 :material_id 13 :cantidad 1 :desperdicio_porcentaje 0 :notas "Base giratoria completa"}
   {:id 2 :bom_id 1 :material_id 14 :cantidad 5 :desperdicio_porcentaje 0 :notas "Juego de 5 ruedas"}
   {:id 3 :bom_id 1 :material_id 3 :cantidad 0.5 :desperdicio_porcentaje 5 :notas "Espuma para asiento"}
   {:id 4 :bom_id 1 :material_id 4 :cantidad 1.5 :desperdicio_porcentaje 8 :notas "Tapicería de asiento y respaldo"}
   {:id 5 :bom_id 1 :material_id 15 :cantidad 1 :desperdicio_porcentaje 0 :notas "Respaldo de malla"}
   {:id 6 :bom_id 1 :material_id 6 :cantidad 12 :desperdicio_porcentaje 3 :notas "Tornillería para ensamble"}
   {:id 7 :bom_id 1 :material_id 7 :cantidad 0.8 :desperdicio_porcentaje 5 :notas "Estructura de aluminio"}
   {:id 8 :bom_id 2 :material_id 1 :cantidad 2.5 :desperdicio_porcentaje 10 :notas "Madera para estructura"}
   {:id 9 :bom_id 2 :material_id 3 :cantidad 0.2 :desperdicio_porcentaje 5 :notas "Espuma para asiento"}
   {:id 10 :bom_id 2 :material_id 4 :cantidad 0.5 :desperdicio_porcentaje 8 :notas "Tapicería de asiento"}
   {:id 11 :bom_id 2 :material_id 6 :cantidad 8 :desperdicio_porcentaje 3 :notas "Tornillería para ensamble"}
   {:id 12 :bom_id 2 :material_id 9 :cantidad 0.1 :desperdicio_porcentaje 5 :notas "Barniz para acabado"}
   {:id 13 :bom_id 3 :material_id 7 :cantidad 3 :desperdicio_porcentaje 5 :notas "Estructura de aluminio"}
   {:id 14 :bom_id 3 :material_id 8 :cantidad 1 :desperdicio_porcentaje 10 :notas "Asiento y respaldo de lona"}
   {:id 15 :bom_id 3 :material_id 6 :cantidad 6 :desperdicio_porcentaje 3 :notas "Tornillería para ensamble"}
   {:id 16 :bom_id 3 :material_id 10 :cantidad 0.15 :desperdicio_porcentaje 5 :notas "Topes y remaches de plástico"}
   {:id 17 :bom_id 4 :material_id 1 :cantidad 3 :desperdicio_porcentaje 10 :notas "Curvas de madera para mecedora"}
   {:id 18 :bom_id 4 :material_id 11 :cantidad 2 :desperdicio_porcentaje 0 :notas "Muelles para asiento"}
   {:id 19 :bom_id 4 :material_id 6 :cantidad 10 :desperdicio_porcentaje 3 :notas "Tornillería para ensamble"}
   {:id 20 :bom_id 4 :material_id 9 :cantidad 0.15 :desperdicio_porcentaje 5 :notas "Barniz para acabado"}
   {:id 21 :bom_id 4 :material_id 12 :cantidad 1 :desperdicio_porcentaje 0 :notas "Lija para acabado"}
   {:id 22 :bom_id 5 :material_id 2 :cantidad 1.5 :desperdicio_porcentaje 10 :notas "Madera para estructura infantil"}
   {:id 23 :bom_id 5 :material_id 3 :cantidad 0.1 :desperdicio_porcentaje 5 :notas "Espuma para asiento"}
   {:id 24 :bom_id 5 :material_id 4 :cantidad 0.3 :desperdicio_porcentaje 8 :notas "Tapicería infantil colorida"}
   {:id 25 :bom_id 5 :material_id 6 :cantidad 6 :desperdicio_porcentaje 3 :notas "Tornillería para ensamble"}
   {:id 26 :bom_id 5 :material_id 9 :cantidad 0.05 :desperdicio_porcentaje 5 :notas "Barniz sellador"}
   {:id 27 :bom_id 5 :material_id 10 :cantidad 0.5 :desperdicio_porcentaje 5 :notas "Respaldo de plástico inyectado"}])

(def proveedores-rows
  [{:id 1 :codigo "PRO-MDN" :nombre "Maderas del Norte S.A." :contacto "Roberto García" :telefono "555-8001" :email "ventas@maderasnorte.com" :direccion "Av. Forestal 123, Durango, DGO" :activo "T"}
   {:id 2 :codigo "PRO-TIM" :nombre "Textiles Industriales de México" :contacto "Laura Martínez" :telefono "555-8002" :email "laura@textilesmx.com" :direccion "Calle de la Industria 456, Tlalnepantla, EDOMEX" :activo "T"}
   {:id 3 :codigo "PRO-TOR" :nombre "Tornillería Torres" :contacto "Pedro Torres" :telefono "555-8003" :email "pedro@torneriatorres.com" :direccion "Eje Central 789, Monterrey, NL" :activo "T"}
   {:id 4 :codigo "PRO-ALS" :nombre "Aluminios del Sur" :contacto "María López" :telefono "555-8004" :email "maria@aluminiosur.com" :direccion "Periférico Sur 234, Guadalajara, JAL" :activo "T"}
   {:id 5 :codigo "PRO-PLI" :nombre "Plásticos Industriales S.A." :contacto "Jorge Hernández" :telefono "555-8005" :email "jorge@plasticosind.com" :direccion "Blvd. del Polímero 567, Querétaro, QRO" :activo "T"}])

(defn ordenes-compra-rows []
  (let [y (seed-year)]
    [{:id 1 :codigo "OC-001" :proveedor_id 1 :fecha (str y "-06-01") :fecha_entrega (str y "-06-15") :estado "pendiente" :notas "Solicitud urgente de madera de roble"}
     {:id 2 :codigo "OC-002" :proveedor_id 2 :fecha (str y "-06-05") :fecha_entrega (str y "-06-20") :estado "aprobada" :notas "Pedido mensual de telas y espuma"}
     {:id 3 :codigo "OC-003" :proveedor_id 3 :fecha (str y "-06-10") :fecha_entrega (str y "-06-18") :estado "recibida" :notas "Reabastecimiento de tornillería"}
     {:id 4 :codigo "OC-004" :proveedor_id 4 :fecha (str y "-06-12") :fecha_entrega (str y "-06-25") :estado "pendiente" :notas "Tubo de aluminio para sillas plegables"}
     {:id 5 :codigo "OC-005" :proveedor_id 5 :fecha (str y "-06-15") :fecha_entrega (str y "-06-30") :estado "aprobada" :notas "Plástico ABS para accesorios"}]))

(def oc-lineas-rows
  [{:id 1 :oc_id 1 :material_id 1 :cantidad 50 :precio_unitario 42.00 :notas "Madera de roble para producción"}
   {:id 2 :oc_id 2 :material_id 3 :cantidad 30 :precio_unitario 30.00 :notas "Espuma de poliuretano"}
   {:id 3 :oc_id 2 :material_id 4 :cantidad 60 :precio_unitario 26.00 :notas "Tela microfibra gris"}
   {:id 4 :oc_id 3 :material_id 6 :cantidad 2000 :precio_unitario 0.12 :notas "Tornillos de acero 1/4"}
   {:id 5 :oc_id 4 :material_id 7 :cantidad 100 :precio_unitario 11.00 :notas "Tubo de aluminio 1 pulgada"}
   {:id 6 :oc_id 5 :material_id 10 :cantidad 50 :precio_unitario 8.00 :notas "Plástico ABS granulado"}])

(def clientes-rows
  [{:id 1 :codigo "CLI-OFI" :nombre "Oficina Total S.A. de C.V." :contacto "Ricardo Mendoza" :telefono "555-9001" :email "compras@oficinatotal.com" :direccion "Av. Reforma 100, Col. Juárez, CDMX" :activo "T"}
   {:id 2 :codigo "CLI-RES" :nombre "Restaurante La Familia" :contacto "Ana María Díaz" :telefono "555-9002" :email "contacto@lafamilia.com" :direccion "Calle Hidalgo 200, Centro, PUE" :activo "T"}
   {:id 3 :codigo "CLI-HOT" :nombre "Hotel Paraíso" :contacto "Carlos Rivera" :telefono "555-9003" :email "compras@hotelparaiso.com" :direccion "Blvd. Costero 500, Cancún, QROO" :activo "T"}
   {:id 4 :codigo "CLI-ESC" :nombre "Escuela Primaria Benito Juárez" :contacto "Prof. Luis Torres" :telefono "555-9004" :email "direccion@escuelabenito.edu" :direccion "Av. Educación 300, Toluca, EDOMEX" :activo "T"}
   {:id 5 :codigo "CLI-HOS" :nombre "Hospital General" :contacto "Dra. Patricia Ruiz" :telefono "555-9005" :email "compras@hospitalgeneral.com" :direccion "Calzada de la Salud 800, Morelia, MICH" :activo "T"}
   {:id 6 :codigo "CLI-CAF" :nombre "Cafetería El Buen Gusto" :contacto "Jorge Ordaz" :telefono "555-9006" :email "elbuengusto@cafe.com" :direccion "Callejón del Café 50, Xalapa, VER" :activo "T"}
   {:id 7 :codigo "CLI-DES" :nombre "Despacho García & Asociados" :contacto "Lic. Fernando García" :telefono "555-9007" :email "contacto@garciaasociados.com" :direccion "Torre Ejecutiva, Piso 15, San Pedro Garza García, NL" :activo "T"}])

(defn pedidos-rows []
  (let [y (seed-year)]
    [{:id 1 :codigo "PED-001" :cliente_id 1 :fecha (str y "-06-01") :fecha_entrega (str y "-06-20") :estado "confirmado" :notas "Entrega en oficinas corporativas"}
     {:id 2 :codigo "PED-002" :cliente_id 2 :fecha (str y "-06-05") :fecha_entrega (str y "-06-25") :estado "pendiente" :notas "Renovación de mobiliario del comedor"}
     {:id 3 :codigo "PED-003" :cliente_id 4 :fecha (str y "-06-10") :fecha_entrega (str y "-07-05") :estado "confirmado" :notas "Equipamiento para nuevo ciclo escolar"}
     {:id 4 :codigo "PED-004" :cliente_id 5 :fecha (str y "-06-12") :fecha_entrega (str y "-06-28") :estado "pendiente" :notas "Sillas para sala de espera"}
     {:id 5 :codigo "PED-005" :cliente_id 7 :fecha (str y "-06-15") :fecha_entrega (str y "-07-01") :estado "pendiente" :notas "Sillas ejecutivas para nueva oficina"}]))

(def pedido-lineas-rows
  [{:id 1 :pedido_id 1 :producto_id 1 :cantidad 10 :precio_unitario 3500.00 :notas "Sillas ejecutivas con reposabrazos"}
   {:id 2 :pedido_id 1 :producto_id 5 :cantidad 5 :precio_unitario 380.00 :notas "Sillas infantiles para sala de espera"}
   {:id 3 :pedido_id 2 :producto_id 2 :cantidad 20 :precio_unitario 1200.00 :notas "Sillas de comedor estándar"}
   {:id 4 :pedido_id 3 :producto_id 5 :cantidad 30 :precio_unitario 380.00 :notas "Sillas infantiles para aulas"}
   {:id 5 :pedido_id 4 :producto_id 3 :cantidad 15 :precio_unitario 450.00 :notas "Sillas plegables para sala de espera"}
   {:id 6 :pedido_id 5 :producto_id 1 :cantidad 8 :precio_unitario 3500.00 :notas "Sillas ejecutivas de gerencia"}])

(defn ordenes-produccion-rows []
  (let [y (seed-year)]
    [{:id 1 :codigo "OP-001" :producto_id 1 :cantidad 10 :fecha_inicio (str y "-06-01") :fecha_fin (str y "-06-14") :estado "completada" :notas "Producción para PED-001 — completada"}
     {:id 2 :codigo "OP-002" :producto_id 2 :cantidad 20 :fecha_inicio (str y "-06-15") :fecha_fin (str y "-06-30") :estado "en_proceso" :notas "Producción para PED-002 — en proceso"}
     {:id 3 :codigo "OP-003" :producto_id 5 :cantidad 30 :fecha_inicio (str y "-06-18") :fecha_fin (str y "-07-04") :estado "en_proceso" :notas "Producción para PED-003 — en proceso"}
     {:id 4 :codigo "OP-004" :producto_id 3 :cantidad 15 :fecha_inicio (str y "-06-22") :fecha_fin (str y "-07-02") :estado "planificada" :notas "Producción para PED-004 — planificada"}
     {:id 5 :codigo "OP-005" :producto_id 1 :cantidad 8 :fecha_inicio (str y "-06-25") :fecha_fin (str y "-07-05") :estado "planificada" :notas "Producción para PED-005 — planificada"}]))

(defn mrp-audit-log-rows []
  (let [y (seed-year)]
    [{:id 4 :entity "materiales" :operation "seed" :data "MRP seed dataset" :user_id 1 :timestamp (str y "-01-01 10:00:00")}
     {:id 5 :entity "productos" :operation "seed" :data "MRP seed dataset" :user_id 1 :timestamp (str y "-01-01 10:00:01")}
     {:id 6 :entity "bom" :operation "seed" :data "MRP seed dataset" :user_id 1 :timestamp (str y "-01-01 10:00:02")}
     {:id 7 :entity "proveedores" :operation "seed" :data "MRP seed dataset" :user_id 1 :timestamp (str y "-01-01 10:00:03")}
     {:id 8 :entity "clientes" :operation "seed" :data "MRP seed dataset" :user_id 1 :timestamp (str y "-01-01 10:00:04")}]))

(defn ^:private non-users-seed-plan []
  [{:table "materiales" :rows materiales-rows}
   {:table "productos" :rows productos-rows}
   {:table "bom" :rows bom-rows}
   {:table "bom_lineas" :rows bom-lineas-rows}
   {:table "proveedores" :rows proveedores-rows}
   {:table "ordenes_compra" :rows (ordenes-compra-rows)}
   {:table "oc_lineas" :rows oc-lineas-rows}
   {:table "clientes" :rows clientes-rows}
   {:table "pedidos" :rows (pedidos-rows)}
   {:table "pedido_lineas" :rows pedido-lineas-rows}
   {:table "ordenes_produccion" :rows (ordenes-produccion-rows)}
   {:table "audit_log" :rows (mrp-audit-log-rows)}])

(def ^:private non-users-clear-order
  ["oc_lineas"
   "ordenes_compra"
   "pedido_lineas"
   "pedidos"
   "ordenes_produccion"
   "bom_lineas"
   "bom"
   "materiales"
   "productos"
   "proveedores"
   "clientes"
   "audit_log"])

(defn- normalize-token [s]
  (some-> s str st/trim (st/replace #"^:+" "") st/lower-case))

(def ^:private vendor->subprotocol
  {"mysql"     #(or (= % "mysql") (= % :mysql))
   "postgres"  #(or (= % "postgresql") (= % :postgresql) (= % "postgres") (= % :postgres))
   "postgresql" #(or (= % "postgresql") (= % :postgresql) (= % "postgres") (= % :postgres))
   "pg"        #(or (= % "postgresql") (= % :postgresql) (= % "postgres") (= % :postgres))
   "sqlite"    #(or (= % "sqlite") (= % :sqlite) (= % "sqlite3") (= % :sqlite3))
   "sqlite3"   #(or (= % "sqlite") (= % :sqlite) (= % "sqlite3") (= % :sqlite3))})

(defn- choose-conn-key
  "Resolve a user token (e.g., nil, pg, :pg, localdb, mysql) to a key in crud/dbs.
  Prefers exact connection keys (e.g., :pg, :localdb, :main, :default). Falls back to
  the first connection whose subprotocol matches a known vendor token. Defaults to :default."
  [token]
  (let [t (normalize-token token)
        dbs crud/dbs
        keys* (set (keys dbs))
        t->key {"default" :default
                "mysql"   :default
                "main"    :main
                "pg"      :pg
                "postgres" :pg
                "postgresql" :pg
                "local"   :localdb
                "localdb" :localdb
                "sqlite"  :localdb
                "sqlite3" :localdb}
        direct (when (seq t)
                 (some (fn [k] (when (= (name k) t) k)) keys*))
        mapped (get t->key t)
        by-vendor (when (seq t)
                    (let [pred (get vendor->subprotocol t)]
                      (when pred
                        (some (fn [[k v]] (when (pred (:subprotocol v)) k)) dbs))))]
    (or direct mapped by-vendor :default)))

(defn populate-tables
  "Populate a table with rows on the selected connection."
  [table rows & {:keys [conn]}]
  (let [conn* (or conn :default)
        table-s (name (keyword table))
        typed-rows (mapv (fn [row]
                           (crud/build-postvars table-s row :conn conn*))
                         rows)]
    (println (format "[database] Seeding %s on connection %s" table-s (name conn*)))
    (try
      (Query! (str "DELETE FROM " table-s) :conn conn*)
      (Insert-multi (keyword table-s) typed-rows :conn conn*)
      (println (format "[database] Seeded %d rows into %s (%s)"
                       (count typed-rows) table-s (name conn*)))
      (catch Exception e
        (println "[ERROR] Seeding failed for" table-s "on" (name conn*) ":" (.getMessage e))
        (throw e)))))

(defn- clear-table
  [table & {:keys [conn]}]
  (let [conn* (or conn :default)
        table-s (name (keyword table))]
    (Query! (str "DELETE FROM " table-s) :conn conn*)
    (println (format "[database] Cleared %s (%s)" table-s (name conn*)))))

(defn- insert-rows
  [table rows & {:keys [conn]}]
  (let [conn* (or conn :default)
        table-s (name (keyword table))
        typed-rows (mapv (fn [row]
                           (crud/build-postvars table-s row :conn conn*))
                         rows)]
    (when (seq typed-rows)
      (Insert-multi (keyword table-s) typed-rows :conn conn*)
      (println (format "[database] Seeded %d rows into %s (%s)"
                       (count typed-rows) table-s (name conn*))))))

(defn- generate-placeholder-image!
  [filepath & {:keys [red green blue]}]
  (let [img (java.awt.image.BufferedImage. 100 60 java.awt.image.BufferedImage/TYPE_INT_RGB)
        g   (.createGraphics img)
        c   (java.awt.Color. (or red 200) (or green 200) (or blue 200))]
    (.setColor g c)
    (.fillRect g 0 0 100 60)
    (.dispose g)
    (javax.imageio.ImageIO/write img "png" (java.io.File. filepath))))

(defn- generate-placeholder-pdf!
  [filepath]
  (let [f (java.io.File. filepath)]
    (io/make-parents f)
    (spit f (str "PDF placeholder for " (.getName f) "\nThis is a demo PDF file generated by contactos."))
    (println (format "[database]   Created placeholder: %s" (.getName f)))))

(defn- seed-placeholder-images!
  [rows table-name column-name]
  (let [uploads-dir (:uploads crud/config)]
    (doseq [row rows]
      (when-let [filename (get row column-name)]
        (let [filepath (str uploads-dir filename)
              id       (:id row)
              r        (mod (* id 73) 256)
              g        (mod (* id 149) 256)
              b        (mod (* id 97) 256)]
          (io/make-parents filepath)
          (generate-placeholder-image! filepath :red r :green g :blue b)
          (println (format "[database]   Created placeholder: %s" filename)))))))

(defn- seed-placeholder-pdfs!
  [rows table-name column-name]
  (let [uploads-dir (:uploads crud/config)]
    (doseq [row rows]
      (when-let [filename (get row column-name)]
        (let [filepath (str uploads-dir filename)]
          (io/make-parents filepath)
          (generate-placeholder-pdf! filepath))))))

(defn seed-non-users
  "Usage:
   - lein seed-non-users
   - lein seed-non-users pg
   - lein seed-non-users localdb

   Seeds all configured tables except users."
  [& args]
  (let [token (first args)
        conn  (choose-conn-key token)
        dbspec (get crud/dbs conn)
        sp (:subprotocol dbspec)]
    (println (format "[database] Seeding non-user tables on connection: %s (subprotocol=%s)" (name conn) sp))
    (doseq [table non-users-clear-order]
      (clear-table table :conn conn))
    (doseq [{:keys [table rows]} (non-users-seed-plan)]
      (insert-rows table rows :conn conn))
    (println "[database] Non-user seed completed.")))

(defn database
  "Usage:
   - lein database                 ; seeds default (mysql per config)
   - lein database pg              ; seeds Postgres (:pg)
   - lein database :pg             ; same as above
   - lein database localdb         ; seeds SQLite (:localdb)"
  [& args]
  (let [token (first args)
        conn  (choose-conn-key token)
        dbspec (get crud/dbs conn)
        sp (:subprotocol dbspec)]
    (println (format "[database] Using connection: %s (subprotocol=%s)" (name conn) sp))
    (populate-tables "users" users-rows :conn conn)
    (println "[database] Done.")))
