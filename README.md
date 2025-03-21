# DB Utilities

This project contains several programs interacting with different relational databases. In principle, this project can be used
against databases of different vendors. Some programs are specific to a given database schema, and some are not.

This project can be useful to try out accessing different database servers from Java programs using JDBC.
One aspect of this is getting a JDBC driver (via Maven) and configuring it. This project can be used
as a template for those JDBC driver configurations (without connection pooling), among other things.

Of course, this project should not be mistaken for a "Java database access library". Personally, for
database access from Java I would prefer [jOOQ](https://www.jooq.org/) or
[Spring JDBC](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html) to
[JPA (Jakarta Persistence)](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2),
because "SQL-orientation" works well in practice and can lead to well performing applications.

## Running containerized database servers

To try out database servers of different vendors, it helps to start with Docker images of those database
products. Below, it is shown how to "install" some database products with Docker.

### PostgreSQL with Docker

See [PostgreSQL Docker setup](https://www.baeldung.com/ops/postgresql-docker-setup) for a good article
on setting up PostgreSQL Docker containers. Also see
[how to use PostgreSQL Docker official image](https://www.docker.com/blog/how-to-use-the-postgres-docker-official-image/).

Here we "install" PostgreSQL as a Docker container, and populate it with this
[sample database](https://neon.tech/postgresql/postgresql-getting-started/postgresql-sample-database).
Many thanks to [neon.tech](https://neon.tech/) for this sample database.

Steps:

```shell
docker pull postgres

mkdir ~/postgresdata

# Note we do not create any volume, in order to avoid "role does not exist" issues.

docker run -d \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  -v ~/postgresdata:/var/lib/postgresql/data \
  --name postgresql \
  postgres

docker exec -it postgresql psql -U postgres

# We are now inside the running postgresql container, inside psql

CREATE DATABASE dvdrental;
# Check database "dvdrental" exists
\list

# Leaving psql and the container
exit

# First cd to the root of this project. Then:

docker cp data/postgresql/dvdrental.tar postgresql:/tmp

docker exec -it postgresql sh

# Inside the container again

pg_restore -U postgres -d dvdrental /tmp/dvdrental.tar

exit

# We have left the running postgres container again

docker exec -it postgresql psql -U postgres

# Inside the container again, inside psql

\c dvdrental

# Displaying the tables
\dt

\d film

# Help
\?

# Querying, but remember to add a semicolon at the end or else it does not work
select count(*) from film;

select film.*, film_actor.*, actor.*
  from film
  left join film_actor on (film.film_id = film_actor.film_id)
  left join actor on (film_actor.actor_id = actor.actor_id)
 where upper(film.title) like 'WA%';

# Querying for XML-only rows

select xmlelement(name film,
           xmlelement(name film_id, film.film_id),
           xmlelement(name title, film.title),
           xmlelement(name description, film.description),
           xmlelement(name release_year, film.release_year),
           xmlelement(name language_id, film.language_id),
           xmlelement(name rental_duration, film.rental_duration),
           xmlelement(name rental_rate, film.rental_rate),
           xmlelement(name length, film.length),
           xmlelement(name replacement_cost, film.replacement_cost),
           xmlelement(name rating, film.rating),
           xmlelement(name last_update, film.last_update),
           xmlelement(name special_features, array_to_string(film.special_features, ', ')),
           xmlelement(name fulltext, film.fulltext),
           xmlelement(name actors,
               xmlagg(
                   xmlelement(name actor,
                       xmlforest(
                           film_actor.actor_id as actor_id,
                           film_actor.last_update as film_actor_last_update,
                           actor.first_name as actor_first_name,
                           actor.last_name as actor_last_name,
                           actor.last_update as actor_last_update
                       )
                   )
               )
           )
       )
  from film
  left join film_actor on (film.film_id = film_actor.film_id)
  left join actor on (film_actor.actor_id = actor.actor_id)
 where upper(film.title) like 'WA%'
 group by film.film_id;

# Querying for JSON-only rows

select json_object(
           'film': json_object(
               'film_id': film.film_id,
               'title': film.title,
               'description': film.description,
               'release_year': film.release_year,
               'language_id': film.language_id,
               'rental_duration': film.rental_duration,
               'rental_rate': film.rental_rate,
               'length': film.length,
               'replacement_cost': film.replacement_cost,
               'rating': film.rating,
               'last_update': film.last_update,
               'special_features': array_to_string(film.special_features, ', '),
               'fulltext': film.fulltext,
               'actors': json_agg(
                   json_object('actor': json_object(
                           'actor_id': film_actor.actor_id,
                           'film_actor_last_update': film_actor.last_update,
                           'actor_first_name': actor.first_name,
                           'actor_last_name': actor.last_name,
                           'actor_last_update': actor.last_update
                       )
                   )
               )
           )
       )
  from film
  left join film_actor on (film.film_id = film_actor.film_id)
  left join actor on (film_actor.actor_id = actor.actor_id)
 where upper(film.title) like 'WA%'
 group by film.film_id;

exit

# We have left the running postgres container again
```

### Db2 with Docker

See [Db2 with Docker](https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker).
Also see [Db2 CE with Docker on Linux](https://www.ibm.com/docs/en/db2/12.1?topic=system-linux).
Follow the steps described in the latter article.

Running the Db2 container (also mentioned in that article, but slightly adapted, e.g. no "restart"):

```shell
mkdir ~/db2data

docker run -d \
  -h db2server \
  --name db2server \
  --restart=no \
  --privileged=true \
  -p 50000:50000 \
  --env-file .env_list \
  -v ~/db2data:/database \
  icr.io/db2_community/db2
```

For a sample database, see
[Db2 sample database](https://www.ibm.com/docs/en/db2/12.1?topic=samples-sample-database).
Many thanks to [ibm.com](https://www.ibm.com) for this sample database.

Use the Db2 CLI:

```shell
docker exec -ti db2server bash -c "su - db2inst1"

# Inside the db2server container

db2

# Inside db2 CLI inside container
# Get help on commands
?

list database directory

connect to SAMPLE

select tabname, tabschema, tbspace from syscat.tables

describe table dept

# ...

quit
exit
```

See [Db2 quick guide](https://www.tutorialspoint.com/db2/db2_quick_guide.htm) for the Db2 CLI.
Note that commands starting with "db2" should be entered without "db2" in front of them when inside
the db2 CLI. More specifically, as described [here](https://www.tutorialspoint.com/db2/db2_server_installation.htm)
(at the end), the Command Line Processor (CLP) can be started in one of three modes. In the example
session above, the interactive mode of "db2" was used.

### Oracle with Docker

See [Oracle Database 23ai Free](https://medium.com/@anders.swanson.93/oracle-database-23ai-free-11abf827ab37).
Also see this [Oracle database tutorial](https://www.oracletutorial.com/).

In particular,
see [create Oracle sample database](https://www.oracletutorial.com/getting-started/create-oracle-sample-database-for-practice/)
for instructions on how to create the sample database from [Oracle database tutorial](https://www.oracletutorial.com/).
Many thanks to [oracletutorial.com](https://www.oracletutorial.com/) for this sample database.

Steps:

```shell
docker pull gvenzl/oracle-free:23.6-slim-faststart

docker volume create oracle-volume

docker run -d \
  --name oracledb \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=testpassword \
  -v oracle-volume:/opt/oracle/oradata \
  gvenzl/oracle-free:23.6-slim-faststart

docker exec -it oracledb bash

# Inside the oracledb container

sqlplus / as sysdba

# Inside sqlplus in the container

select table_name, tablespace_name from user_tables;

# ...

quit
exit

# It is time to create the sample database
# See https://www.oracletutorial.com/getting-started/create-oracle-sample-database-for-practice/

# First cd to the root of this project. Then:

docker cp data/oracle/ot_create_user.sql oracledb:/tmp
docker cp data/oracle/ot_schema.sql oracledb:/tmp
docker cp data/oracle/ot_data.sql oracledb:/tmp
docker cp data/oracle/ot_drop.sql oracledb:/tmp

docker exec -it oracledb bash
sqlplus / as sysdba

# Inside sqlplus inside the container again

show con_name;

# FREEPDB1 is the default pluggable database in this Oracle Docker container
alter session set container = FREEPDB1;
# Check that this pluggable database has been selected
show con_name;

# No need to execute command "alter database open" here

@/tmp/ot_create_user.sql

connect ot@FREEPDB1;

@/tmp/ot_schema.sql
@/tmp/ot_data.sql

# We can see the created tables now
SELECT table_name FROM user_tables ORDER BY table_name;

exit
exit
```

See [fix ora-12505](https://www.atlassian.com/data/databases/how-to-fix-ora-12505-tns-listener-does-not-currently-know-of-sid-given-in-connect-descriptor)
in case of ORA-12505 errors. Also see
[connecting to CDB and PDB](https://oracle-base.com/articles/12c/multitenant-connecting-to-cdb-and-pdb-12cr1).

## Reference material
