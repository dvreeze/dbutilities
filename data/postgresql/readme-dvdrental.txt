Sample database from https://neon.tech/postgresql/postgresql-getting-started/postgresql-sample-database.

After creating the database above, it was exported as follows:

docker exec -it postgresql sh

# Inside the container:

pg_dump --schema-only -U postgres -d dvdrental > /tmp/dvdrental-schema.sql
exit

# Left the container again

docker cp postgresql:/tmp/dvdrental-schema.sql ./data/postgresql/
