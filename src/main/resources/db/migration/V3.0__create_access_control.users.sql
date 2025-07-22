/* Create Tables */

CREATE TABLE access_control.users (
  usr_id BIGSERIAL NOT NULL,
  system_user_id UUID NOT NULL UNIQUE,
  locked_user_ind varchar(10) NOT NULL,
  sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null),
  created_by UUID NOT NULL,
  created_date timestamp with time zone NOT NULL,
  modified_by UUID NOT NULL,
  modified_date timestamp with time zone NOT NULL
);

/* Implement Temporal Table */

CREATE TABLE access_control.users_history (LIKE access_control.users, hist_id BIGSERIAL NOT NULL);

CREATE TRIGGER versioning_trigger_users
  BEFORE INSERT OR UPDATE OR DELETE ON access_control.users
  FOR EACH ROW EXECUTE PROCEDURE versioning(‘sys_period’, ‘access_control.users_history’, true);

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE access_control.users ADD CONSTRAINT PK_users PRIMARY KEY (usr_id);

CREATE INDEX “system_user_id_index” ON access_control.users (system_user_id);