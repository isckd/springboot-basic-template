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