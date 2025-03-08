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
\dt

# The tables are displayed

exit

# We have left the running postgres container again
```

### Db2 with Docker

See [Db2 with Docker](https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker).
Also see [Db2 CE with Docker on Linux](https://www.ibm.com/docs/en/db2/12.1?topic=system-linux).
Follow the steps described in the latter article.

Running the Db2 container (also mentioned in that article, but slightly adapted):

```shell
mkdir ~/db2data

docker run -d \
  -h db2server \
  --name db2server \
  --restart=always \
  --privileged=true \
  -p 50000:50000 \
  --env-file .env_list \
  -v ~/db2data:/database \
  icr.io/db2_community/db2
```

For a sample database, see
[Db2 sample database](https://www.ibm.com/docs/en/db2/12.1?topic=samples-sample-database).

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
the db2 CLI.

## Reference material
