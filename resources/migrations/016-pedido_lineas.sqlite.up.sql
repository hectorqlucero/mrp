CREATE TABLE IF NOT EXISTS pedido_lineas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  pedido_id INTEGER NOT NULL REFERENCES pedidos(id),
  producto_id INTEGER NOT NULL REFERENCES productos(id),
  cantidad INTEGER NOT NULL DEFAULT 1,
  precio_unitario REAL DEFAULT 0,
  notas TEXT,
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
