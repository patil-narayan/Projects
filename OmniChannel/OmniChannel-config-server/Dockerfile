FROM alpine
RUN apk update && apk add openjdk17-jre-headless
WORKDIR /opt/oc
COPY target/Omnichannel-config-server.jar /opt/oc/
CMD ["sh", "-c", "java -jar -Dspring.profiles.active=dev Omnichannel-config-server.jar"]
