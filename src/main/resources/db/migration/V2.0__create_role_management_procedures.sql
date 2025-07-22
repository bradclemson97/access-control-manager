-- =======================================================================================
-- Procedure: insert_role_inheritance
-- This procedure is used to associate a Permission with 1 or more child Capabilities.
--
-- Calling it more than once with the same parent permission does not replace any previous
-- role inheritance rows
--
-- Params:
--   permission_role_name: the role_name of the parent permission.
--   child_roles: an ARRAY[] of role_names of child capability roles.
--
-- Returns: VOID
--
-- Example: (to associate a capability called 'Test Capability' with the
-- permission 'Core Access')
--
--      PERFORM insert_role_inheritance('Core Access', ARRAY['Test Capability']);
--
-- =======================================================================================
CREATE OR REPLACE FUNCTION insert_role_inheritance(permission_role_name VARCHAR, child_roles VARCHAR[])
    RETURNS VOID AS $$
DECLARE
    permission_role_id INTEGER;
    capability_role_id INTEGER;
    role_count INTEGER;
BEGIN

    IF permission_role_name IS NULL THEN
        RAISE EXCEPTION 'Permission name cannot be null';
    END IF;

    SELECT roe_id INTO permission_role_id FROM access_control.roles
        WHERE role_name = permission_role_name AND role_type_code = 'PERMISSION';

    IF permission_role_id IS NULL THEN
        RAISE EXCEPTION 'Permission % not found.', permission_role_name;
    END IF;

    SELECT COUNT(*) INTO role_count FROM access_control.roles
        WHERE role_type_code = 'CAPABILITY'
        AND role_name in (select unnest(child_roles));

    IF role_count < array_length(child_roles, 1) THEN
        RAISE EXCEPTION '% child roles supplied and % found. %',
            array_length(child_roles, 1), role_count, child_roles;
    END IF;

    FOR capability_role_id IN (SELECT roe_id FROM access_control.roles
                                WHERE role_type_code = 'CAPABILITY'
                                    AND role_name IN (SELECT unnest(child_roles)))
        LOOP
            INSERT INTO access_control.role_inheritance (roe_id, inherits_roe_id, inheritance_type_code, created_by, created_date, modified_by, modified_date)
            VALUES (permission_role_id, capability_role_id, 'INHERIT', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now());
        END LOOP;
END;
$$ LANGUAGE plpgsql;

-- =======================================================================================
-- Procedure: insert_role_assignment
-- This procedure is used to make 1 or more Permission assignable from the supplied
-- parent permission, so that users who have the parent permission can give the 'assignable'
-- permission to other users.
--
-- As above, calling it more than once with the same parent permission does not replace
-- any previous assignments...
--
-- Params:
--   permission_role_name the role_name of the parent permission.
--   assignable_roles an ARRAY[] of role_names of assignable permission roles.
--
-- Returns: VOID
--
-- Example: (to allow a user with 'Core Access' to assign users a permission called
-- 'Test Permission')
--
--      PERFORM insert_role_assignment('Core Access', ARRAY['Test Permission']);
--
-- =======================================================================================
CREATE OR REPLACE FUNCTION insert_role_assignment(permission_role_name VARCHAR, assignable_roles VARCHAR[])
    RETURNS VOID AS $$
DECLARE
    permission_role_id INTEGER;
    assignable_role_id INTEGER;
    role_count INTEGER;
BEGIN
    IF permission_role_name IS NULL THEN
        RAISE EXCEPTION 'Permission name cannot be null';
    END IF;

    SELECT roe_id INTO permission_role_id FROM access_control.roles
    WHERE role_name = permission_role_name AND role_type_code = 'PERMISSION';

    IF permission_role_id IS NULL THEN
        RAISE EXCEPTION 'Permission % not found.', permission_role_name;
    END IF;

    SELECT COUNT(*) INTO role_count FROM access_control.roles
        WHERE role_type_code = 'PERMISSION'
        AND role_name IN (SELECT unnest(assignable_roles));

    IF role_count < array_length(assignable_roles, 1) THEN
        RAISE EXCEPTION '% assignable roles supplied and % found. %',
        array_length(assignable_roles, 1), role_count, assignable_roles;
    END IF;

    FOR assignable_role_id IN (SELECT roe_id FROM access_control.roles
                                WHERE role_type_code = 'PERMISSION'
                                    AND role_name IN (SELECT unnest(assignable_roles)))
        LOOP
            INSERT INTO access_control.role_inheritances (roe_id, inherits_roe_id, inheritance_type_code, created_by, created_date, modified_by, modified_date)
            VALUES (permission_role_id, assignable_role_id, 'ASSIGNMENT', '${data-creator-system-user-id}', now(), '${data-creator-system-user-id}', now());
        END LOOP;
END;
$$ LANGUAGE plpgsql;
