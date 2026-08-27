# Étape 1 : build de l'application avec Maven Wrapper
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app
COPY . .

RUN ./mvnw clean package -DskipTests

# Étape 2 : image d'exécution, allégée (JRE seul, pas le JDK complet)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
