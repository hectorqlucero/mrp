CREATE TABLE IF NOT EXISTS productos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT UNIQUE,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  precio_venta REAL DEFAULT 0,
  activo TEXT NOT NULL DEFAULT 'T',
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
