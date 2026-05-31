#!/usr/bin/env bash
set -euo pipefail

if [ -f /var/lib/mysql/.mall_ease_inited ]; then
  echo "database already initialized"
  exit 0
fi

mysql -uroot <<SQL
CREATE DATABASE IF NOT EXISTS mall_ease DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'%' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON mall_ease.* TO '${DB_USERNAME}'@'%';
FLUSH PRIVILEGES;
SQL

if [ -f /app/sql/mall-ease.sql ]; then
  mysql -uroot mall_ease < /app/sql/mall-ease.sql
fi

touch /var/lib/mysql/.mall_ease_inited
echo "database initialized"
