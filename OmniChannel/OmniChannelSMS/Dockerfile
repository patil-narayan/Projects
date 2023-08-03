FROM alpine
RUN apk update && apk add openjdk17-jre-headless
WORKDIR /opt/oc
COPY target/OmniChannelSMS-0.0.1.jar /opt/oc/
CMD ["sh", "-c", "java -jar -Dspring.profiles.active=dev OmniChannelSMS-0.0.1.jar"]
