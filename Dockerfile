FROM azul/zulu-openjdk-alpine:11
WORKDIR /usr/app/
RUN apk add fontconfig ttf-dejavu
COPY ./build/libs/spiral-knights-api-*-withDependencies.jar .
EXPOSE 8080
ENTRYPOINT exec java -Djava.awt.headless=true -jar /usr/app/spiral-knights-api-*-withDependencies.jar