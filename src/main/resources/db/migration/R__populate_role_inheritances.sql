TRUNCATE TABLE access_control.role_inheritances;

DO
$$
    BEGIN
        PERFORM insert_role_inheritance('User Core Access',
            ARRAY[
                'Search and View users',
                'Sign in',
                'Sign out',
                'Manage own password',
                'Update own user profile']);

        PERFORM insert_role_inheritance('Customer Maintenance',
            ARRAY[
                'Manage customers',
                'Create customer',
                'Update customer']);

        PERFORM insert_role_inheritance('User Administration',
            ARRAY[
                'Create users',
                'Manage permissions given to a user',
                'Manager users account']);

        -- Role Assignments
        PERFORM insert_role_assignment('User Administration',
            (SELECT array_agg(role_name)
            from access_control.roles where role_type_code = 'PERMISSION'));

    END
$$ LANGUAGE plpgsql;