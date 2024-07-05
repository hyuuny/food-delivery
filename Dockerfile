FROM openjdk:17-jdk
WORKDIR /app
RUN mkdir -p /app/build/libs
COPY build/libs/*.jar /app/application.jar
EXPOSE 8080
CMD ["java", "-jar", "application.jar"]
