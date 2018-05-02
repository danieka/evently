FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/evently.jar /evently/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/evently/app.jar"]
