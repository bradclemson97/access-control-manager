INSERT INTO access_control.roles(role_name, role_type_code, description, created_by, created_date, modified_by, modified_date)
VALUES
    ('User Core Access', 'PERMISSION', 'Basic system access', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Customer Maintenance', 'PERMISSION', 'Permission to maintain customers', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('User Administration', 'PERMISSION', 'Permission to maintain users', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now());