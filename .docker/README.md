# docker-compose config

# docker commands

run `postgresql`:
```
docker run --name docker_postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 -v /docker/volumes/postgres:/var/lib/postgresql/data postgres:12.2
```

run `pgadmin`:
```
docker run -p 3080:80 --name pgadmin -e 'PGADMIN_DEFAULT_EMAIL=user@domain.com' -e 'PGADMIN_DEFAULT_PASSWORD=SuperSecret' -d dpage/pgadmin4
```