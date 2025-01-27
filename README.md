# Environment
springboot-basic-template 
(springboot 3.3.4, JDK 21, Kotlin 1.9.22, gradle 8.5)

<br>
<br>

---

<br>
<br>

# First Settingz
settings.gradle.kts - rootProject.name = {projectName}  <br>
application.yml <br>
- spring.application.name = {projectName}
- server.port = {port}

<br>
<br>

---

<br>
<br>

# Local build and run

## Docker

### docker pull

```
docker pull gradle:8.5-jdk21
docker pull openjdk:21-jdk
```

### docker build and run

```
docker build -t springboot-basic-template .
docker run -p 8080:8080 --name springboot-basic-template springboot-basic-template
```

<br>
<br>

---

<br>
<br>


# Logging

## HTTP Request, Response Logging
하나의 HTTP 요청에 생성된 쓰레드에 전역적으로 `TraceId` 부여
앱/웹 요청에서 API Gateway 인입 시 TraceId 를 생성하여 해당 HTTP 요청에 관련된 모든 micro services 들의 로그를 유니크하게 인식하기 위함.
- HTTP Header : X-Trace-Id    (없을 경우 내부적으로 생성)
- yyyymmddHHmmss-{UUID 7자리}

Datetime LogLevel Thread className T[TraceId] - [REQ/RES] HTTPMethod URI RequestBody 
    Headers
와 같은 형식으로 출력 (상세 : CustomRequestLoggingFilter, logback-spring.xml)
```
2024-11-23 23:52:00.418 INFO  [http-nio-8080-exec-1] c.e.d.c.CustomRequestLoggingFilter T[20241123235200-8138947] - [SBT-REQ] POST /api/books {"id":null,"name":"Test Book","category":"Test Category"}
    Headers={accept-encoding=gzip, user-agent=ReactorNetty/1.1.22, host=localhost:8080, accept=*, x-component-name=SBT, content-type=application/json, content-length=57}
2024-11-23 23:52:00.503 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL T[20241123235200-8138947] - 
    select
        next value for books_seq
2024-11-23 23:52:00.528 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL T[20241123235200-8138947] - 
    insert 
    into
        books
        (category, name, id) 
    values
        (?, ?, ?)    
2024-11-23 23:52:00.545 INFO  [http-nio-8080-exec-1] c.e.d.c.CustomRequestLoggingFilter T[20241123235200-8138947] - [SBT-RES] 200 126ms /api/books {"id":1,"name":"Test Book","category":"Test Category"}
    Headers={X-Trace-Id=20241123235200-8138947}    
```

## DB Connection Pool, HTTP Connection Pool
10초 scheduling

```
2024-11-23 13:55:28.303 INFO  [scheduling-1] c.e.d.config.ConnectionPoolMonitor T[] - [HTTP Connection Pool] active=0, corePoolSize=10, poolSize=10, maximumPoolSize=200, taskCount=2, completedTaskCount=2, queueSize=0
2024-11-23 13:55:28.303 INFO  [scheduling-1] c.e.d.config.ConnectionPoolMonitor T[] - [DB Connection Pool]: Active=0, Idle=10, Total=10, Awaiting=0, MaxPoolSize=10 
```

<br>
<br>

---

<br>
<br>

# HTTP Error Handling
GlobalExceptionHandler <br>
ErrorCode 에 ErrorType, HttpStatus, Message 를 정의하여 사용 <br>
정의한 BusinessException 또는 정의하지 않은 Exception 발생 시 ErrorResponse 을 반환 <br>

```
{
  "returnCode": "B001",
  "returnMessage": "해당 책을 찾을 수 없습니다."
}
``` 

<br>
<br>

---

<br>
<br>


# TODO
Author, Review 관련 API 추가, Service 로직 작성 <br>
WebClient 사용 예시 <br>
테스트코드 고도화 <br>
OpenAPI Generator, REDOC 적용
