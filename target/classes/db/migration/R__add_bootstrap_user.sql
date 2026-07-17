DO $$
    DECLARE
        user_id INTEGER;
        role_id INTEGER;
    BEGIN
        -- Insert the user
        INSERT INTO access_control.users
        (created_by, created_date, modified_by, modified_date, system_user_id)
        VALUES
            ('${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now(), '${bootstrap-system-user-id}')
        ON CONFLICT (system_user_id)
            DO UPDATE SET modified_by = EXCLUDED.modified_by, modified_date = EXCLUDED.modified_date
        RETURNING usr_id INTO user_id;

        -- Remove any roles this user may currently have, and insert them again
        DELETE from access_control.user_roles where usr_id = user_id;

        -- Give the user 'Core Access' and 'User Administration'
        FOR role_id IN (SELECT roe_id FROM access_control.roles WHERE roles.role_type_code = 'PERMISSION')
            LOOP
                INSERT INTO access_control.user_roles (roe_id, usr_id, created_by, created_date, modified_by, modified_date)
                VALUES
                    (role_id, user_id, '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now());
            END LOOP;

    END $$: