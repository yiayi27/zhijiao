FROM anapsix/alpine-java:8_server-jre_unlimited


ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD gruul-*-service/target/*.jar integral.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" , "-Xms512m","-Xmx512m" ,"/integral.jar"]

