FROM adoptopenjdk/openjdk11:alpine-slim AS build
WORKDIR /app
COPY . ./
RUN ./gradlew --no-daemon --stacktrace clean bootJar

FROM adoptopenjdk/openjdk11:alpine-jre
RUN apk add --no-cache bash
RUN apk add --update --no-cache ttf-dejavu
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --chown=spring:spring --from=build /app/build/libs/*.jar app.jar
USER spring:spring
ENTRYPOINT java -jar app.jar
