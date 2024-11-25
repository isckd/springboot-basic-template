package com.example.demo.common.error

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val returnCode: String,
    val returnMessage: String
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                returnCode = errorCode.code,
                returnMessage = errorCode.message
            )
        }
    }
}
