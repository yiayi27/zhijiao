FROM openjdk:8-alpine

RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

ADD gruul-recycling-service/target/*.jar gruul-recycling.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" , "-Xms512m","-Xmx512m" ,"/gruul-recycling.jar"]