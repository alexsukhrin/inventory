-- :name version :? :1
SELECT @@VERSION

 -- :name get-table :? :*
SELECT id, service, accounting, property_type, "name", serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, TO_CHAR((last_changes AT TIME ZONE 'UTC' AT TIME ZONE 'Europe/Kiev'), 'YYYY-MM-DD HH24:MI') AS last_changes, photo_url FROM inventory order by id desc

-- :name get-record :? :1
select id, service, accounting, property_type, "name", serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, TO_CHAR((last_changes AT TIME ZONE 'UTC' AT TIME ZONE 'Europe/Kiev'), 'YYYY-MM-DD HH24:MI') AS last_changes, photo_url FROM inventory where id = :row-id

-- :name delete-row :? :*
delete from inventory where id = :row-id

-- :name add-record :? :1
INSERT INTO inventory (service, accounting, property_type, name, serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, last_changes, photo_url) 
VALUES (:service, :accounting, :property_type, :name, :serial_number, :id_label, :actual_location, :status, :department, :notes, :mvo, :nomenclature, :price, :accounting_name, :unit, :invoice, :last_changes, :photo_url) 
RETURNING id, service, accounting, property_type, "name", serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, TO_CHAR((last_changes AT TIME ZONE 'UTC' AT TIME ZONE 'Europe/Kiev'), 'YYYY-MM-DD HH24:MI') AS last_changes, photo_url

-- :name update-record :? :1
UPDATE inventory SET service = :service, accounting = :accounting, property_type = :property_type, "name" = :name, serial_number = :serial_number, id_label = :id_label, actual_location = :actual_location, status = :status, department = :department, notes = :notes, mvo = :mvo, nomenclature = :nomenclature, price = :price, accounting_name = :accounting_name, unit = :unit, invoice = :invoice, last_changes = :last_changes, photo_url = :photo_url WHERE id = :row-id 
RETURNING id, service, accounting, property_type, "name", serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, TO_CHAR((last_changes AT TIME ZONE 'UTC' AT TIME ZONE 'Europe/Kiev'), 'YYYY-MM-DD HH24:MI') AS last_changes, photo_url

 -- :name get-report :? :*
SELECT id, service, accounting, property_type, "name", serial_number, id_label, actual_location, status, department, notes, mvo, nomenclature, price, accounting_name, unit, invoice, TO_CHAR((last_changes AT TIME ZONE 'UTC' AT TIME ZONE 'Europe/Kiev'), 'YYYY-MM-DD HH24:MI') AS last_changes FROM inventory order by id