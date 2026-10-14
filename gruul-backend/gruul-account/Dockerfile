FROM openjdk:8-alpine

RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

ADD gruul-account-service/target/*.jar gruul-account.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" , "-Xms1024m","-Xmx1024m" ,"/gruul-account.jar"]