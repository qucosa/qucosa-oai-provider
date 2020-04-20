#!/bin/bash

echo "psql.url=$PSQL_URL" > docker.properties

if [ ! -z "$PSQL_USER" ]
then
  echo "psql.user=$PSQL_USER" >> docker.properties
fi

if [ ! -z "$PSQL_PASSWD" ]
then
  echo "psql.passwd=$PSQL_PASSWD" >> docker.properties
fi

if [ ! -z "$MIN_POOL_SIZE" ]
then
  echo "min.pool.size=$MIN_POOL_SIZE" >> docker.properties
fi

if [ ! -z "$MAX_POOL_SIZE" ]
then
  echo "max.pool.size=$MAX_POOL_SIZE" >> docker.properties
fi

if [ ! -z "$APP_URL" ]
then
  echo "app.url=$APP_URL" >> docker.properties
fi

for i in $@; do
  if [ -f "$i" ]
  then
    u="${u:+$u,}file://$i"
  fi
done

if [ ! -z "$u" ]
then
  java -jar qucosa-oai-provider-0.0.1-SNAPSHOT.jar "-Dspring.config.location=./docker.properties, $u"
else
  java -jar qucosa-oai-provider-0.0.1-SNAPSHOT.jar "-Dspring.config.location=./docker.properties"
fi