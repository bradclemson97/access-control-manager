DROP TABLE IF EXISTS access_control.roles CASCADE;

CREATE TABLE access_control.roles (
    roe_id BIGSERIAL NOT NULL,
    role_name varchar(50) NOT NULL UNIQUE,
    role_type_code varchar(10) NOT NULL, -- Permission or Capability
    description varchar(1000) NOT NULL,
    created_by UUID NOT NULL,
    created_date timestamp with time zone NOT NULL,
    modified_by UUID NOT NULL,
    modified_date timestamp with time zone NOT NULL
);

ALTER TABLE access_control.roles ADD CONSTRAINT roe_pk PRIMARY KEY (roe_id);

ALTER TABLE access_control.roles ADD CONSTRAINT roe_uk UNIQUE (role_name);

CREATE INDEX "role_name_index" ON access_control.roles (role_name);