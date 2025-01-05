CREATE TABLE IF NOT EXISTS inventory (
    id SERIAL PRIMARY KEY,
    service VARCHAR(255),                 -- Служба
    accounting VARCHAR(255),              -- Облік
    property_type VARCHAR(255),           -- Тип майна
    name VARCHAR(255),                    -- Найменування
    serial_number VARCHAR(255),           -- Серійний номер
    id_label VARCHAR(255),                -- ID|Наліпка
    actual_location VARCHAR(255),         -- Фактично знаходиться
    status VARCHAR(50),                   -- Статус
    department VARCHAR(255),              -- Підрозділ
    notes TEXT,                           -- Примітки та дописи
    mvo VARCHAR(255),                     -- М.В.О.
    nomenclature VARCHAR(255),            -- Номенклатура
    price VARCHAR(255),                   -- Ціна
    accounting_name VARCHAR(255),         -- Найменування по обліку
    unit VARCHAR(50),                     -- Одиниця
    invoice VARCHAR(255),                 -- Накладна
    last_changes TIMESTAMP,               -- Останні зміни
    photo_url TEXT                        -- Фото
);
