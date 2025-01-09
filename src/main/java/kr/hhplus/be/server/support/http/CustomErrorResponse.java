package kr.hhplus.be.server.support.http;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description="API 실패 응답")
public class CustomErrorResponse {
    @Schema(description = "성공 여부", example = "false")
    private final boolean success = false;
    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int statusCode;
    @Schema(description = "오류 메시지", example = "잘못된 입력값입니다.")
    private final String message;

    /**
     * 예외 발생 시 상테코드 + 예외 메시지 전달
     *
     * @param errorCode
     * @return
     */
    public static CustomErrorResponse of(ErrorCode errorCode){
        return new CustomErrorResponse(errorCode.getHttpStatus().value(), errorCode.getMessage());
    }
}
