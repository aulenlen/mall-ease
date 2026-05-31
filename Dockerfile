FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /src

COPY . .

RUN mvn -B -DskipTests clean install

RUN for module in \
    mall-ease-gateway \
    mall-ease-user \
    mall-ease-product \
    mall-ease-trade \
    mall-ease-content \
    mall-ease-marketing \
    mall-ease-search \
    mall-ease-bff; \
    do mvn -B -pl "$module" -DskipTests package spring-boot:repackage; done

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    gzip \
    mysql-server \
    netcat-openbsd \
    procps \
    redis-server \
    supervisor \
    tar \
    wget \
    && rm -rf /var/lib/apt/lists/*

ARG NACOS_VERSION=2.3.2
ARG ES_VERSION=8.12.2

RUN wget -q "https://github.com/alibaba/nacos/releases/download/${NACOS_VERSION}/nacos-server-${NACOS_VERSION}.tar.gz" \
    && tar -xzf "nacos-server-${NACOS_VERSION}.tar.gz" -C /opt \
    && mv /opt/nacos /opt/nacos-server \
    && rm "nacos-server-${NACOS_VERSION}.tar.gz"

RUN wget -q "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-${ES_VERSION}-linux-x86_64.tar.gz" \
    && tar -xzf "elasticsearch-${ES_VERSION}-linux-x86_64.tar.gz" -C /opt \
    && mv "/opt/elasticsearch-${ES_VERSION}" /opt/elasticsearch \
    && rm "elasticsearch-${ES_VERSION}-linux-x86_64.tar.gz"

RUN useradd -r -m esuser \
    && mkdir -p /data/es /var/log/mall-ease/es \
    && chown -R esuser:esuser /data/es /var/log/mall-ease/es /opt/elasticsearch

RUN printf '%s\n' \
    'discovery.type: single-node' \
    'xpack.security.enabled: false' \
    'network.host: 127.0.0.1' \
    'http.port: 9200' \
    'path.data: /data/es' \
    'path.logs: /var/log/mall-ease/es' \
    > /opt/elasticsearch/config/elasticsearch.yml

RUN mkdir -p /app/services /app/sql /app/deploy/docker /var/log/mall-ease

COPY --from=build /src/mall-ease-gateway/target/mall-ease-gateway-*.jar /app/services/gateway.jar
COPY --from=build /src/mall-ease-user/target/mall-ease-user-*.jar /app/services/user.jar
COPY --from=build /src/mall-ease-product/target/mall-ease-product-*.jar /app/services/product.jar
COPY --from=build /src/mall-ease-trade/target/mall-ease-trade-*.jar /app/services/trade.jar
COPY --from=build /src/mall-ease-content/target/mall-ease-content-*.jar /app/services/content.jar
COPY --from=build /src/mall-ease-marketing/target/mall-ease-marketing-*.jar /app/services/marketing.jar
COPY --from=build /src/mall-ease-search/target/mall-ease-search-*.jar /app/services/search.jar
COPY --from=build /src/mall-ease-bff/target/mall-ease-bff-*.jar /app/services/bff.jar

COPY document/sql/mall-ease.sql /app/sql/mall-ease.sql
COPY deploy/docker/ /app/deploy/docker/

RUN chmod +x /app/deploy/docker/start.sh /app/deploy/docker/init-db.sh

ENV SPRING_PROFILES_ACTIVE=cloud
ENV GATEWAY_PORT=7860
ENV NACOS_SERVER_ADDR=127.0.0.1:8848
ENV NACOS_NAMESPACE=public
ENV NACOS_GROUP=DEFAULT_GROUP
ENV DB_URL="jdbc:mysql://127.0.0.1:3306/mall_ease?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
ENV DB_USERNAME=mall
ENV DB_PASSWORD=mall_ease_demo
ENV REDIS_HOST=127.0.0.1
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=
ENV REDIS_DATABASE=0
ENV ES_URIS=http://127.0.0.1:9200
ENV SA_TOKEN_SECRET=please-change-this-secret-at-least-32-characters
ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC"
ENV ES_JAVA_OPTS="-Xms512m -Xmx512m"

EXPOSE 7860 9051

CMD ["bash", "/app/deploy/docker/start.sh"]
