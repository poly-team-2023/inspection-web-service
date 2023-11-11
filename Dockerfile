FROM openjdk:17-oracle
VOLUME /tmp
COPY  build/libs/inspection-web-service-0.0.1-SNAPSHOT.jar inspection-web-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/inspection-web-service-0.0.1-SNAPSHOT.jar"]