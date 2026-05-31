#!/usr/bin/env bash
set -euo pipefail

mkdir -p /var/log/mall-ease /var/run/mysqld /data/es
chown -R mysql:mysql /var/run/mysqld /var/lib/mysql
chown -R esuser:esuser /data/es /var/log/mall-ease/es

if [ ! -d /var/lib/mysql/mysql ]; then
  mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
fi

mysqld_safe --datadir=/var/lib/mysql --bind-address=127.0.0.1 --sql-mode=NO_ENGINE_SUBSTITUTION > /var/log/mall-ease/mysql-init.log 2>&1 &

for i in $(seq 1 90); do
  if mysqladmin ping --silent; then
    break
  fi
  if [ "$i" = "90" ]; then
    echo "mysql did not become ready in time"
    exit 1
  fi
  sleep 2
done

bash /app/deploy/docker/init-db.sh

mysqladmin -uroot shutdown || true
sleep 5

exec supervisord -c /app/deploy/docker/supervisord.conf
