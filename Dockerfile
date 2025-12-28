# Этап сборки
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS build
WORKDIR /app

# Копируем исходники
COPY pom.xml .
COPY src ./src
COPY mvnw ./
COPY .mvn ./.mvn

RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw clean package -DskipTests

FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
