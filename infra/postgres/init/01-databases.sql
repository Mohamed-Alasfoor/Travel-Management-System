CREATE USER users_app WITH PASSWORD 'users_dev_only';
CREATE USER travels_app WITH PASSWORD 'travels_dev_only';
CREATE USER payments_app WITH PASSWORD 'payments_dev_only';
CREATE DATABASE users_db OWNER users_app;
CREATE DATABASE travels_db OWNER travels_app;
CREATE DATABASE payments_db OWNER payments_app;

CREATE USER engagement_app WITH PASSWORD 'engagement_dev_only';
CREATE DATABASE engagement_db OWNER engagement_app;
