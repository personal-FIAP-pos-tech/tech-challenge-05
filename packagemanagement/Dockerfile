# Etapa 1: compila o projeto e gera o jar
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY lombok.config ./
COPY config/ config/
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests -Djacoco.skip=true

# Etapa 2: imagem final apenas com o JRE e o jar
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /app/target/packagemanagement-*.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
