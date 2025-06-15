#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER evgeniabulis WITH PASSWORD '04041995';
    CREATE DATABASE my_budget;
    GRANT ALL PRIVILEGES ON DATABASE my_budget TO evgeniabulis;
EOSQL