package kr.hhplus.be.server.support.exception;

import kr.hhplus.be.server.support.http.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessException(BusinessException ex) {
        CustomErrorResponse errorResponse = CustomErrorResponse.of(ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getHttpStatus());
    }
}
