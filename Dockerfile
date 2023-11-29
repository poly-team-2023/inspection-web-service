FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=target/inspection-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
