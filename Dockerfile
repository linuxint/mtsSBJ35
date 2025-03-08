# Build stage
FROM ghcr.io/graalvm/graalvm-ce:23 as builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build --no-daemon

# Run stage
FROM ghcr.io/graalvm/graalvm-ce:23-ol9
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Add wait-for-it script for database connection check
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]