package com.example.demo.common.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // Common Errors
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // Validation Errors
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "V001", "입력 값 검증에 실패했습니다."),

    // Repository Errors
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "R001", "이미 존재하는 리소스입니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "R002", "데이터 무결성 제약 조건을 위반했습니다."),

    // Service Errors
    BUSINESS_LOGIC_ERROR(HttpStatus.BAD_REQUEST, "S001", "비즈니스 로직 처리 중 오류가 발생했습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "S002", "접근 권한이 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "S003", "요청이 거부되었습니다."),

    // Book-specific Errors
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "해당 책을 찾을 수 없습니다."),
    INVALID_BOOK_DATA(HttpStatus.BAD_REQUEST, "B005", "유효하지 않은 책 정보입니다.")
}
