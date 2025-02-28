# Подготовка базы данных

## Создание базы данных

```sql
\set ON_ERROR_STOP on

\echo 'Creating owner'
create user splate_event_collector_ms_main;

\echo 'Creating database'
create database splate_event_collector_ms_main owner splate_event_collector_ms_main;

\echo 'Creating roles'
create role splate_event_collector_ms_main_readonly;
create role splate_event_collector_ms_main_readwrite;

\connect splate_event_collector_ms_main;

\echo 'Creating schemas'
drop schema public;
create schema main authorization splate_event_collector_ms_main;
create schema migration authorization splate_event_collector_ms_main;
create schema events authorization splate_event_collector_ms_main;

\echo 'Granting permissions'
grant connect on database splate_event_collector_ms_main to splate_event_collector_ms_main_readonly;
grant usage on schema main to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema main grant select on sequences to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema main grant execute on functions to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema main grant usage on types to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema main grant select, references on tables to splate_event_collector_ms_main_readonly;

grant usage on schema events to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema events grant select on sequences to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema events grant execute on functions to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema events grant usage on types to splate_event_collector_ms_main_readonly;
alter default privileges for user splate_event_collector_ms_main in schema events grant select, references on tables to splate_event_collector_ms_main_readonly;

grant connect on database splate_event_collector_ms_main to splate_event_collector_ms_main_readwrite;
grant usage on schema main to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema main grant usage, select on sequences to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema main grant execute on functions to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema main grant usage on types to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema main grant select, insert, update, delete, truncate, references, trigger on tables to splate_event_collector_ms_main_readwrite;

grant connect on database splate_event_collector_ms_main to splate_event_collector_ms_main_readwrite;
grant usage, create on schema events to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema events grant usage, select on sequences to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema events grant execute on functions to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema events grant usage on types to splate_event_collector_ms_main_readwrite;
alter default privileges for user splate_event_collector_ms_main in schema events grant select, insert, update, delete, truncate, references, trigger on tables to splate_event_collector_ms_main_readwrite;

\echo 'Creating extensions'
create extension "uuid-ossp" schema main;
create extension "uuid-ossp" schema events;

\echo 'Create users'
create user splate_event_collector_ms_main_default;
grant splate_event_collector_ms_main_readwrite to splate_event_collector_ms_main_default;

\echo 'Set default schema'
alter user splate_event_collector_ms_main set search_path = 'main';
alter user splate_event_collector_ms_main_default set search_path = 'main';

\c postgres

```

## Установка паролей для пользователей базы данных

```sql
alter user splate_event_collector_ms_main with PASS='';
alter user splate_event_collector_ms_main_default with PASS='';

```
