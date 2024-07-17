FROM maven:3.9.7-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml /app/pom.xml
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn install -DskipTests

FROM builder AS prepare-production
RUN mkdir -p target/dependency
WORKDIR /app/target/dependency
RUN jar -xf ../*.jar

FROM eclipse-temurin:21

VOLUME /tmp
ARG DEPENDENCY=/app/target/dependency
COPY --from=prepare-production ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=prepare-production ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=prepare-production ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","de.htw.paymentservice.PaymentServiceApplication"]