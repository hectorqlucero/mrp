CREATE TABLE IF NOT EXISTS bom_lineas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  bom_id INTEGER NOT NULL REFERENCES bom(id),
  material_id INTEGER NOT NULL REFERENCES materiales(id),
  cantidad REAL NOT NULL DEFAULT 1,
  desperdicio_porcentaje REAL DEFAULT 0,
  notas TEXT,
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
