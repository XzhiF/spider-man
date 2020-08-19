FROM java:8
RUN mkdir /logs
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Xms256M -Xmx256M"
ENV SPRING_OPTS=""
ENTRYPOINT java ${JAVA_OPTS}  ${SPRING_OPTS} -Dlogging.file.path=/logs -jar /app.jar


#docker run \
#-d \
#-e SPRING_OPTS='-Dserver.port=8080 -Dspring.profiles.active=test' \
#-p 8080:8080 \
#-v /opt/docker-workspace/spider-man-docker/apps/spider-man-gateway:/logs \
#--link=nacos-standalone-mysql:nacos.spiderman.xzf \
#--link=redis5:redis.spiderman.xzf \
#--network spider-man-docker_default \
#--name spider-man-gateway \
#registry.cn-shenzhen.aliyuncs.com/xzf1989/spider-man-gateway


