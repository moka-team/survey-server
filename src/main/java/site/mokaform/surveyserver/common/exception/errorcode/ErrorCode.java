package site.mokaform.surveyserver.common.exception.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String name();

    String getCode();

    HttpStatus getHttpStatus();

    String getMessage();
}
