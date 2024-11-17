FROM amazoncorretto:21 as BUILDER
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:21
ENV TZ=Asia/Seoul

COPY --from=BUILDER /build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
