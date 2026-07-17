INSERT INTO access_control.roles(role_name, role_type_code, description, created_by, created_date, modified_by, modified_date)
VALUES
    ('Search and View users', 'CAPABILITY', 'Search and View users', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Sign in', 'CAPABILITY', 'Sign in', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Sign out', 'CAPABILITY', 'Sign out', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Manage own password', 'CAPABILITY', 'Manage own password', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Update own user profile', 'CAPABILITY', 'Update own user profile', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Manage customers', 'CAPABILITY', 'Manage customers', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Create customer', 'CAPABILITY', 'Create customer', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Update customer', 'CAPABILITY', 'Update customer', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Create users', 'CAPABILITY', 'Create User', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Manage permissions given to a user', 'CAPABILITY', 'Manage permissions given to a user', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now()),
    ('Manage users account', 'CAPABILITY', 'Manage users account', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now());