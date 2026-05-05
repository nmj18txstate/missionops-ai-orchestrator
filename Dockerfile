FROM maven:3.9.8-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package
EXPOSE 8080
CMD ["mvn","-pl","missionops-api","spring-boot:run"]
