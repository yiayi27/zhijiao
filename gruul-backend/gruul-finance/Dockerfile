FROM openjdk:8-alpine

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ADD gruul-*-service/target/*.jar finance.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" ,"-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/tmp/dump/finance", "-Xms256m","-Xmx256m" ,"/finance.jar"]