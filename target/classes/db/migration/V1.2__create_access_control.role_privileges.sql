CREATE TABLE access_control.role_privileges (
    rpe_id BIGSERIAL NOT NULL,
    rse_id BIGINT NOT NULL,
    roe_id BIGINT NOT NULL,
    description varchar(200) NULL,
    created_by UUID NOT NULL,
    created_date timestamp with time zone NOT NULL,
    modified_by UUID NOT NULL,
    modified_date timestamp with time zone NOT NULL
);

ALTER TABLE access_control.role_privileges ADD CONSTRAINT rpe_pk PRIMARY KEY (rpe_id);

CREATE INDEX "IXrpe_roe_fk" ON access_control.role_privileges (roe_id ASC);

CREATE INDEX "IXrpe_rse_fk" ON access_control.role_privileges (rse_id ASC);

ALTER TABLE access_control.role_privileges ADD CONSTRAINT rpe_roe_fk
    FOREIGN KEY (roe_id) REFERENCES access_control.roles (roe_id) ON DELETE No Action ON UPDATE No Action;