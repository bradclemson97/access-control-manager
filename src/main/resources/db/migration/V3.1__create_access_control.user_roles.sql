/* Create Tables */

CREATE TABLE access_control.user_roles (
    ure_id BIGSERIAL NOT NULL,
    roe_id BIGINT NOT NULL,
    usr_id BIGINT NOT NULL,
    sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null),
    created_by UUID NOT NULL,
    created_date timestamp with time zone NOT NULL,
    modified_by UUID NOT NULL,
    modified_date timestamp with time zone NOT NULL
);

CREATE TABLE access_control.user_roles_history (LIKE access_control.user_roles, hist_id BIGSERIAL NOT NULL);

CREATE TRIGGER versioning_trigger_user_roles
  BEFORE INSERT OR UPDATE OR DELETE ON access_control.user_roles
  FOR EACH ROW EXECUTE PROCEDURE versioning(‘sys_period’, ‘access_control.user_roles_history’, true);

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE access_control.user_roles ADD CONSTRAINT ure_pk PRIMARY KEY (ure_id);

CREATE INDEX "IXFK_ure_roe_fk" ON access_control.user_roles (roe_id ASC);

CREATE INDEX "IXFK_ure_usr_fk" ON access_control.user_roles (usr_id ASC);

ALTER TABLE access_control.user_roles ADD CONSTRAINT ure_usr_fk
    FOREIGN KEY (usr_id) REFERENCES access_control.users (usr_id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE access_control.user_roles ADD CONSTRAINT ure_roe_fk
    FOREIGN KEY (roe_id) REFERENCES access_control.roles (roe_id) ON DELETE No Action ON UPDATE No Action;