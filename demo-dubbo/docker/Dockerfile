FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

ENV MAVEN_OPTS="-Dmaven.wagon.http.retryHandler.count=3 -Dmaven.wagon.http.retryHandler.requestSentEnabled=true -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 -Dmaven.wagon.http.pool=false"

RUN mkdir -p /root/.m2 && \
    printf '%s\n' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' \
    '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
    '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>ali-public</id>' \
    '      <mirrorOf>central</mirrorOf>' \
    '      <url>https://maven.aliyun.com/repository/public</url>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' \
    > /root/.m2/settings.xml

COPY pom.xml .
COPY demo-dubbo/pom.xml demo-dubbo/
COPY demo-dubbo/dubbo-api/pom.xml demo-dubbo/dubbo-api/
COPY demo-dubbo/dubbo-provider/pom.xml demo-dubbo/dubbo-provider/
COPY demo-dubbo/dubbo-consumer/pom.xml demo-dubbo/dubbo-consumer/

RUN mvn -f pom.xml install -N -B

COPY demo-dubbo/dubbo-api/src ./demo-dubbo/dubbo-api/src
COPY demo-dubbo/dubbo-provider/src ./demo-dubbo/dubbo-provider/src
COPY demo-dubbo/dubbo-consumer/src ./demo-dubbo/dubbo-consumer/src

RUN mvn -f demo-dubbo/pom.xml clean package -DskipTests -B

# Provider镜像
FROM eclipse-temurin:17-jre-alpine AS provider

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /build/demo-dubbo/dubbo-provider/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8081 20880

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=5 \
  CMD wget --spider -q http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]

# Consumer镜像
FROM eclipse-temurin:17-jre-alpine AS consumer

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /build/demo-dubbo/dubbo-consumer/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8082

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=5 \
  CMD wget --spider -q http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
