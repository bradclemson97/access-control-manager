CREATE TABLE access_control.role_inheritances (
    rie_id BIGSERIAL NOT NULL,
    roe_id BIGINT NOT NULL,
    inherits_roe_id BIGINT NOT NULL,
    inheritance_type_code varchar(10) NOT NULL,
    created_by UUID NOT NULL,
    created_date timestamp with time zone NOT NULL,
    modified_by UUID NOT NULL,
    modified_date timestamp with time zone NOT NULL
);

ALTER TABLE access_control.role_inheritances ADD CONSTRAINT rie_pk PRIMARY KEY (rie_id);

CREATE INDEX "IXFK_rie_roe_fk" ON access_control.role_inheritances (roe_id ASC);

CREATE INDEX "IXFK_rie_roe_inherit_fk" ON access_control.role_inheritances (inherits_roe_id ASC);

ALTER TABLE access_control.role_inheritances ADD CONSTRAINT rie_roe_fk
    FOREIGN KEY (roe_id) REFERENCES access_control.roles (roe_id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE access_control.role_inheritances ADD CONSTRAINT rie_roe_inherit_fk
    FOREIGN KEY (inherits_roe_id) REFERENCES access_control.roles (roe_id) ON DELETE No Action ON UPDATE No Action;