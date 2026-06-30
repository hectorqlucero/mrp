CREATE TABLE IF NOT EXISTS oc_lineas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  oc_id INTEGER NOT NULL REFERENCES ordenes_compra(id),
  material_id INTEGER NOT NULL REFERENCES materiales(id),
  cantidad REAL NOT NULL DEFAULT 1,
  precio_unitario REAL DEFAULT 0,
  notas TEXT,
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
