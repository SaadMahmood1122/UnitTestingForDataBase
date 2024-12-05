FROM openjdk:17
EXPOSE 8080
ADD ./build/libs/* app.jar

ENTRYPOINT ["java","-jar","./app.jar"]