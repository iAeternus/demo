FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY dubbo-api/pom.xml dubbo-api/
COPY dubbo-provider/pom.xml dubbo-provider/
COPY dubbo-consumer/pom.xml dubbo-consumer/

RUN mvn dependency:go-offline -B

COPY dubbo-api/src ./dubbo-api/src
COPY dubbo-provider/src ./dubbo-provider/src
COPY dubbo-consumer/src ./dubbo-consumer/src

RUN mvn clean package -DskipTests -B

# Provider镜像
FROM eclipse-temurin:17-jre-alpine AS provider

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /build/dubbo-provider/target/*.jar app.jar

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

COPY --from=builder /build/dubbo-consumer/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8082

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=5 \
  CMD wget --spider -q http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
