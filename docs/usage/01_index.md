# Пример использования

TODO

```sql
create table events."dd11ca28-c7d5-407d-a102-aa77f90f9593"
(
    id                                     uuid         not null primary key,
    version                                varchar(255) not null,
    severity                               int          not null,
    name                                   varchar(255) not null,
    "3dd8abb8-7459-493b-b017-15e1ee94efac" TEXT
);

```
