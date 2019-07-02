FROM openjdk:10
ADD target/docker-artistopedia.jar docker-artistopedia.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","docker-artistopedia.jar"]