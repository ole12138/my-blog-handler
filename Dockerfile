# syntax=docker/dockerfile:1.4

#FROM --platform=$BUILDPLATFORM maven:3.9.7-eclipse-temurin-22 AS build
FROM maven:3.9.7-eclipse-temurin-22 AS build-dependencies
WORKDIR /builddir
COPY . /builddir/
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
'>/builddir/m2_settings.xml
#ARG MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public,https://maven.aliyun.com/repository/spring,https://maven.aliyun.com/repository/spring-plugin,https://repo.maven.apache.org/maven2"
RUN mvn --settings /builddir/m2_settings.xml clean package -X



######################################################################################
#extract-keyword
#FROM bellsoft/liberica-runtime-container:jre-21-crac-slim-musl
FROM eclipse-temurin:21-jdk-jammy
ARG DEPENDENCY=/builddir
ARG BASEDIR=/workdir
RUN mkdir -p $BASEDIR/input
WORKDIR $BASEDIR
COPY --from=build-dependencies ${DEPENDENCY}/extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar $BASEDIR/extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar

COPY --from=build-dependencies ${DEPENDENCY}/handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar $BASEDIR/handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar

COPY --from=build-dependencies ${DEPENDENCY}/handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar $BASEDIR/handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar

#Add pandoc symlinks and install runtime dependencies
RUN apt-get -q --no-allow-insecure-repositories update \
  && DEBIAN_FRONTEND=noninteractive \
     apt-get install --assume-yes --no-install-recommends pandoc

COPY --from=build-dependencies ${DEPENDENCY}/upload-blog/upload-wordpress/target/upload-wordpress-1.0-SNAPSHOT.jar $BASEDIR/upload-blog/upload-wordpress/target/upload-wordpress-1.0-SNAPSHOT.jar

ENV JAVA=/opt/java/openjdk/bin/java
ENV PANDOC=pandoc
ENV EXTRACT_KEYWORD_JAR=$BASEDIR/extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar
ENV GET_FRONT_MATTER=$BASEDIR/handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar
ENV SET_FRONT_MATTER=$BASEDIR/handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar
ENV UPLOAD_WORDPRESS=$BASEDIR/handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar

COPY ./script/test.sh $BASEDIR/entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
CMD ["./input", "./out"]