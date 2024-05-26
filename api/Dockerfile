# Builder
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar) && ls


# Production
FROM eclipse-temurin:21-jre-alpine as production
VOLUME /tmp

RUN addgroup -S appuser && adduser -S appuser -G appuser

ARG DEPENDENCY=/workspace/app/target/dependency
COPY --chown=appuser:appuser --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --chown=appuser:appuser --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --chown=appuser:appuser --from=build ${DEPENDENCY}/BOOT-INF/classes /app

USER appuser
ENTRYPOINT ["java","-cp","app:app/lib/*", "-Dspring.profiles.active=prod", "io.mattinfern0.kanbanboardapi.KanbanboardapiApplication"]
