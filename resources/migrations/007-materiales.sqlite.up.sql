CREATE TABLE IF NOT EXISTS materiales (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT UNIQUE,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  tipo TEXT NOT NULL DEFAULT 'otro',
  unidad_medida TEXT NOT NULL DEFAULT 'pieza',
  costo_unitario REAL DEFAULT 0,
  stock_actual REAL DEFAULT 0,
  stock_minimo REAL DEFAULT 0,
  ubicacion TEXT,
  activo TEXT NOT NULL DEFAULT 'T',
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
