package kr.hhplus.be.server.support.exception;

import kr.hhplus.be.server.support.constant.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
        logException();
    }

    /**
     * 에러 코드 그룹별로 로깅 레벨을 다르게 설정
     */
    private void logException() {
        String logMessage = String.format("[%s] %s - %s",
                errorCode.name(),
                errorCode.getMessage(),
                detailMessage);

        switch (errorCode.getHttpStatus().value()) {
            // 4xx 클라이언트 에러
            case 400 -> // Bad Request
                    log.warn(logMessage);
            case 403 -> // Forbidden
                    log.warn(logMessage);
            case 404 -> // Not Found
                    log.warn(logMessage);

            // 5xx 서버 에러
            case 500 -> // Internal Server Error
                    log.error(logMessage);
            default ->
                    log.error("Unhandled error: {}", logMessage);
        }
    }
}
