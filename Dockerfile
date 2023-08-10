FROM openjdk:18
EXPOSE 8080
COPY target/enoca-1.0.0.jar enoca.jar
ENTRYPOINT ["java","-jar","/enoca.jar"]