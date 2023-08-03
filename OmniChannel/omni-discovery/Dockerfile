FROM alpine
RUN apk update && apk add openjdk17-jre-headless
WORKDIR /opt/oc
COPY target/omni-discovery-0.0.1-SNAPSHOT.jar /opt/oc/
CMD ["sh", "-c", "java -jar omni-discovery-0.0.1-SNAPSHOT.jar"]
