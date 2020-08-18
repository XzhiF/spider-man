FROM java:8
RUN mkdir /logs
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Xms256M -Xmx256M"
ENV SPRING_OPTS=""
ENTRYPOINT java ${JAVA_OPTS}  ${SPRING_OPTS} -Dlogging.file.path=/logs -jar /app.jar


# docker run -e SPRING_OPTS='-Dserver.port=8888 -Dspring.profiles.active=test' -p 8888:8888 -v /Users/xzf/Projects/dockerprj2020/spider-man-docker/spider-man-admin:/logs  xzf/spider-man-admin