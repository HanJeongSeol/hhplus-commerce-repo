package kr.hhplus.be.server.support.exception;

import kr.hhplus.be.server.support.constant.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public BusinessException(ErrorCode errorCode, Object... args) {
        this(errorCode, errorCode.formatMessage(args));
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
