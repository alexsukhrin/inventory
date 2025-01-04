INSERT INTO permissions (role_id, operation, resource)
VALUES
(1, 'CREATE', 'ALL'), -- Admin: create all tables
(1, 'UPDATE', 'ALL'), -- Admin: edit all tables
(1, 'DELETE', 'ALL'), -- Admin: delete all tables
(1, 'READ', 'ALL'),   -- Admin: read all tables

(2, 'UPDATE', 'SPECIFIC_FIELDS'), -- User 1: edit specific fields
(2, 'READ', 'ALL'),               -- User 1: read all tables

(3, 'READ', 'ALL');               -- User 2: read only
