DO $$
    DECLARE
        userId BIGINT;
        roleId BIGINT;
    BEGIN

        INSERT INTO access_control.users (system_user_id, locked_user_ind, created_by, created_date, modified_by, modified_date)
        VALUES (
            '${bootstrap-system-user-id}'::UUID,
            'NA',
            '${data-creator-system-user-id}'::UUID,
            now(),
            '${data-creator-system-user-id}'::UUID,
            now()
        )
        ON CONFLICT (system_user_id)
            DO UPDATE SET modified_by = EXCLUDED.modified_by, modified_date = EXCLUDED.modified_date
        RETURNING usr_id INTO userId;

        -- Wipe and re-grant so re-runs stay idempotent
        DELETE FROM access_control.user_roles WHERE usr_id = userId;

        -- Grant every PERMISSION role — each PERMISSION inherits a set of CAPABILITYs,
        -- so this gives the bootstrap user the full capability set
        FOR roleId IN (SELECT roe_id FROM access_control.roles WHERE role_type_code = 'PERMISSION')
            LOOP
                INSERT INTO access_control.user_roles (roe_id, usr_id, created_by, created_date, modified_by, modified_date)
                VALUES (
                    roleId,
                    userId,
                    '${data-creator-system-user-id}'::UUID,
                    now(),
                    '${data-creator-system-user-id}'::UUID,
                    now()
                );
            END LOOP;

    END $$;
