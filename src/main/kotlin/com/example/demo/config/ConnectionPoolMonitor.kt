package com.example.demo.config

import com.zaxxer.hikari.HikariDataSource
import org.apache.tomcat.util.threads.ThreadPoolExecutor
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
@ConditionalOnClass(ServletWebServerApplicationContext::class)
class ConnectionPoolMonitor(
    private val webServerContext: ServletWebServerApplicationContext,
    private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    fun monitorConnectionPools() {
        logHttpConnectionPool()
        logDbConnectionPool()
    }

    private fun logHttpConnectionPool() {
        val tomcatWebServer = webServerContext.webServer as TomcatWebServer
        val tomcat = tomcatWebServer.tomcat
        val connector = tomcat.connector
        val protocolHandler = connector.protocolHandler
        
        if (protocolHandler is org.apache.coyote.http11.Http11NioProtocol) {
            val executor = protocolHandler.executor as ThreadPoolExecutor
            
            log.info(
                "[HTTP Connection Pool] active=${executor.activeCount}, " +
                "corePoolSize=${executor.corePoolSize}, " +
                "poolSize=${executor.poolSize}, " +
                "maximumPoolSize=${executor.maximumPoolSize}, " +
                "taskCount=${executor.taskCount}, " +
                "completedTaskCount=${executor.completedTaskCount}, " +
                "queueSize=${executor.queue.size}"
            )
        }
    }

    private fun logDbConnectionPool() {
        if (dataSource is HikariDataSource) {
            val pool = dataSource.hikariPoolMXBean
            
            log.info(
                "[DB Connection Pool]: Active=${pool.activeConnections}, " +
                "Idle=${pool.idleConnections}, " +
                "Total=${pool.totalConnections}, " +
                "Awaiting=${pool.threadsAwaitingConnection}, " +
                "MaxPoolSize=${dataSource.maximumPoolSize}"
            )
        }
    }
}
