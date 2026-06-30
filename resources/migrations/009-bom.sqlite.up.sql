CREATE TABLE IF NOT EXISTS bom (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT UNIQUE,
  producto_id INTEGER NOT NULL REFERENCES productos(id),
  descripcion TEXT,
  activo TEXT NOT NULL DEFAULT 'T',
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
