# 빌드 스테이지
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
# 의존성 캐시를 위해 Gradle 빌드를 실행. 실패 시에도 다음 단계로 진행
RUN gradle build --no-daemon || return 0
# 전체 소스 코드를 컨테이너의 작업 디렉토리로 복사
COPY . .
RUN gradle build --no-daemon

# 실행 스테이지
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
