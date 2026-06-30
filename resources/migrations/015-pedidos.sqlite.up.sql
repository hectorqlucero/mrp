CREATE TABLE IF NOT EXISTS pedidos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT UNIQUE,
  cliente_id INTEGER NOT NULL REFERENCES clientes(id),
  fecha TEXT DEFAULT (date('now')),
  fecha_entrega TEXT,
  estado TEXT NOT NULL DEFAULT 'pendiente',
  notas TEXT,
  created_by INTEGER,
  created_at TEXT DEFAULT (datetime('now')),
  modified_by INTEGER,
  modified_at TEXT DEFAULT (datetime('now'))
);
