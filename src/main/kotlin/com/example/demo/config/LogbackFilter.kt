package com.example.demo.config

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class LogbackFilter : Filter<ILoggingEvent>() {
    override fun decide(event: ILoggingEvent): FilterReply {
        return if (event.formattedMessage.contains("/actuator/prometheus")) {
            FilterReply.DENY
        } else {
            FilterReply.NEUTRAL
        }
    }
}
