CREATE TABLE IF NOT EXISTS ordenes_produccion (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT UNIQUE,
  producto_id INTEGER NOT NULL REFERENCES productos(id),
  cantidad INTEGER NOT NULL DEFAULT 1,
  fecha_inicio TEXT,
  fecha_fin TEXT,
  estado TEXT NOT NULL DEFAULT 'planificada',
  notas TEXT,
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
