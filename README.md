# Environment
springboot-basic-template 
(springboot 3.3.4, JDK 21, Kotlin 1.9.22, gradle 8.5)

---

# Local build and run

## Docker

### docker pull
docker pull gradle:8.5-jdk21
docker pull openjdk:21-jdk

### docker build and run
docker build -t springboot-basic-template .
docker run -p 8080:8080 --name springboot-basic-template springboot-basic-template

---


# Logging

## HTTP Request, Response Logging
하나의 HTTP 요청에 생성된 쓰레드에 전역적으로 `TraceId` 부여
앱/웹 요청에서 API Gateway 인입 시 TraceId 를 생성하여 해당 HTTP 요청에 관련된 모든 micro services 들의 로그를 유니크하게 인식하기 위함.
- HTTP Header : X-Trace-Id    (없을 경우 내부적으로 생성)
- yyyymmddHHmmss-{UUID 7자리}

Datetime LogLevel Thread className T[TraceId] - [REQ/RES] HTTPMethod URI RequestBody 
    Headers
와 같은 형식으로 출력 (상세 : logback-spring.xml)
```
2024-11-23 14:01:47.288 INFO  [http-nio-8080-exec-9] c.e.d.c.CustomRequestLoggingFilter T[20241123140045-bb8a3c4] - [REQ] POST /api/books { "id": 1, "name": "The Lord of the Rings", "category": "Fantasy2"} 
	Headers={host=localhost:8080, user-agent=curl/7.87.0, accept=*/*, content-type=application/json, x-trace-id=20241123140045-bb8a3c4, content-length=74}
2024-11-23 14:01:47.291 INFO  [http-nio-8080-exec-9] c.e.d.c.CustomRequestLoggingFilter T[20241123140045-bb8a3c4] - [RES] 200 3ms /api/books {"id":1,"name":"The Lord of the Rings","category":"Fantasy2"} 
	Headers={X-Trace-Id=20241123140045-bb8a3c4}    
```

## DB Connection Pool, HTTP Connection Pool
10초 scheduling

```
2024-11-23 13:55:28.303 INFO  [scheduling-1] c.e.d.config.ConnectionPoolMonitor T[] - [HTTP Connection Pool] active=0, corePoolSize=10, poolSize=10, maximumPoolSize=200, taskCount=2, completedTaskCount=2, queueSize=0
2024-11-23 13:55:28.303 INFO  [scheduling-1] c.e.d.config.ConnectionPoolMonitor T[] - [DB Connection Pool]: Active=0, Idle=10, Total=10, Awaiting=0, MaxPoolSize=10 
```

# TODO
restTemplate / restClinet 등으로 HTTP 요청 보낼 때 헤더값에 TraceId, componentName 부여
HTTP Reqeust 요청 받을 시 componentName 처리해서 [componentName-REQ/RES] 와 같이 출력

전역 에러 처리 적용
Actuator 적용