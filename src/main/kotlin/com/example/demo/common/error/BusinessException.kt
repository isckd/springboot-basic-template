package com.example.demo.common.error

class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
