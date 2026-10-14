FROM anapsix/alpine-java:8_server-jre_unlimited

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ADD gruul-logistics-service/target/gruul-logistics.jar gruul-logistics.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" , "-Xms512m","-Xmx512m" ,"/gruul-logistics.jar"]
