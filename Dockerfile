FROM openjdk:17-oracle AS build
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ["./mvnw", "package", "-Dmaven.test.skip=true"]

FROM openjdk:17-oracle
EXPOSE 8080
COPY --from=build /app/target/inspection-web-service-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
