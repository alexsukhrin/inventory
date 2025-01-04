CREATE TABLE IF NOT EXISTS rows (
    id SERIAL PRIMARY KEY,
    table_id INT NOT NULL REFERENCES tables(id) ON DELETE CASCADE,
    column_id INT NOT NULL REFERENCES columns(id) ON DELETE CASCADE,
    row_number INT NOT NULL,
    value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (table_id, column_id, row_number)
);