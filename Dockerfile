# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Se copia primero el pom.xml para descargar dependencias y aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Se copia el código fuente y se empaqueta el microservicio omitiendo los tests
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Se copia únicamente el archivo .jar generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exposición del puerto interno de Spring Boot
EXPOSE 8080

# Comando de ejecución de la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]