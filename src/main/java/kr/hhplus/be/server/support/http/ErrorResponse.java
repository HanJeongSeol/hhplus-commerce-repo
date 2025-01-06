package kr.hhplus.be.server.support.http;

import kr.hhplus.be.server.support.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final boolean success = false;
    private final int statusCode;
    private final String message;

    /**
     * 예외 발생 시 상테코드 + 예외 메시지 전달
     *
     * @param errorCode
     * @return
     */
    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.getMessage());
    }
}
