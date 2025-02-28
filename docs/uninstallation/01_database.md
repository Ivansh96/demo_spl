# Удаление БД

```sql
\echo 'Terminating dangling connections'
select pid, pg_terminate_backend(pid) from pg_stat_activity where pid <> pg_backend_pid();

\echo 'Switch to default database'
\c postgres

\echo 'Dropping database'
drop owned by splate_event_collector_ms_main;
drop owned by splate_event_collector_ms_main_default;
drop database splate_event_collector_ms_main;

\echo 'Dropping users'
drop user splate_event_collector_ms_main;
drop user splate_event_collector_ms_main_default;

\echo 'Dropping roles'
drop user splate_event_collector_ms_main_readonly;
drop user splate_event_collector_ms_main_readwrite;

```
