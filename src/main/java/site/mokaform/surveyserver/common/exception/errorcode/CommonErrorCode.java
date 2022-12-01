package site.mokaform.surveyserver.common.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
    INVALID_PARAMETER("C001", HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND("C002", HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR("C003", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST("C004", HttpStatus.BAD_REQUEST, "Invalid request"),
    EXPIRED_ACCESS_TOKEN("C005", HttpStatus.FORBIDDEN, "Access token is expired"),
    ILLEGAL_TOKEN("C006", HttpStatus.FORBIDDEN, "Illegal token"),
    LOGGED_OUT_ACCESS_TOKEN("C007", HttpStatus.BAD_REQUEST, "This access token has been logged out."),
    NOT_EXPIRED_ACCESS_TOKEN("C008", HttpStatus.BAD_REQUEST, "Access token is not expired"),
    ILLEGAL_REFRESH_TOKEN("C009", HttpStatus.FORBIDDEN, "Illegal refresh token. Login required"),
    ACCESS_TOKEN_NOT_EXIST("C010", HttpStatus.BAD_REQUEST, "Access token in header required."),
    REFRESH_TOKEN_NOT_EXIST("C011", HttpStatus.BAD_REQUEST, "Refresh token in cookie required."),
    INVALID_FILE_EXTENSION("C012", HttpStatus.BAD_REQUEST, "올바르지 않은 파일 확장자입니다. (png, jpg, jpeg 가능)"),
    ERROR_UPLOAD_FILE("C013", HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    CommonErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
