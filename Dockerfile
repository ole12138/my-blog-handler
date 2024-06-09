# syntax=docker/dockerfile:1.4

#FROM --platform=$BUILDPLATFORM maven:3.9.7-eclipse-temurin-22 AS build
FROM maven:3.9.7-eclipse-temurin-22 AS build-dependencies
WORKDIR /workdir/server
COPY . /workdir/server/
RUN echo mvn -verison
RUN echo '\n\
<settings>\n\
  <mirrors>\n\
    <mirror>\n\
      <id>aliyunmaven</id>\n\
      <mirrorOf>*</mirrorOf>\n\
      <name>阿里云公共仓库</name>\n\
      <url>https://maven.aliyun.com/repository/public</url>\n\
    </mirror>\n\
  </mirrors>\n\
</settings>\n\
'>/workdir/server/m2_settings.xml
#ARG MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public,https://maven.aliyun.com/repository/spring,https://maven.aliyun.com/repository/spring-plugin,https://repo.maven.apache.org/maven2"
RUN mvn --settings /workdir/server/m2_settings.xml clean package -X

#COPY src /workdir/server/src
#
#RUN mvn --batch-mode clean compile assembly:single
#
#FROM build AS dev-envs
#RUN <<EOF
#apt-get update
#apt-get install -y --no-install-recommends git
#EOF
#
#RUN <<EOF
#useradd -s /bin/bash -m vscode
#groupadd docker
#usermod -aG docker vscode
#EOF
## install Docker tools (cli, buildx, compose)
#COPY --from=gloursdocker/docker / /
#CMD ["java", "-jar", "target/app.jar" ]
#
######################################################################################
#FROM eclipse-temurin:17-jre-focal
FROM bellsoft/liberica-runtime-container:jre-21-crac-slim-musl as extract-keyword
ARG DEPENDENCY=/workdir/server/extract-keyword/target
#EXPOSE 8080
COPY --from=build-dependencies ${DEPENDENCY}/extract-keyword-1.0-SNAPSHOT.jar /app.jar
#CMD java -jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]


######################################################################################
FROM bellsoft/liberica-runtime-container:jre-21-crac-slim-musl as get-front-matter
ARG DEPENDENCY=/workdir/server/handle-front-matter/get-front-matter/target
#EXPOSE 8080
COPY --from=build-dependencies ${DEPENDENCY}/get-front-matter-1.0-SNAPSHOT.jar /app.jar
#CMD java -jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]


######################################################################################
FROM bellsoft/liberica-runtime-container:jre-21-crac-slim-musl as set-front-matter
ARG DEPENDENCY=/workdir/server/handle-front-matter/set-front-matter/target
#EXPOSE 8080
COPY --from=build-dependencies ${DEPENDENCY}/set-front-matter-1.0-SNAPSHOT.jar /app.jar
#CMD java -jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]


######################################################################################
FROM bellsoft/liberica-runtime-container:jre-21-crac-slim-musl as upload-wordpress
ARG DEPENDENCY=/workdir/server/upload-blog/upload-wordpress/target
#EXPOSE 8080
COPY --from=build-dependencies ${DEPENDENCY}/upload-wordpress-1.0-SNAPSHOT.jar /app.jar
#CMD java -jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]