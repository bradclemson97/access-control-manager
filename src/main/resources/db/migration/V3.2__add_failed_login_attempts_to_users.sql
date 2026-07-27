ALTER TABLE access_control.users ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
ALTER TABLE access_control.users_history ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
