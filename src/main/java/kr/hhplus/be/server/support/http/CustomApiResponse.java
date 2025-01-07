package kr.hhplus.be.server.support.http;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.SuccessCode;
import lombok.Getter;

@Getter
@Schema(description = "API 성공 응답")
public class CustomApiResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int statusCode;
    @Schema(description = "응답 메시지", example = "정상 처리되었습니다.")
    private final String message;
    @Schema(description = "응답 데이터")
    private final T data;

    public CustomApiResponse(SuccessCode successCode, T data) {
        this.success = true;
        this.statusCode = successCode.getHttpStatusCode();
        this.message = successCode.getMessage();
        this.data = data;
    }

    public static <T> CustomApiResponse<T> of(SuccessCode successCode, T data){
        return new CustomApiResponse<>(successCode, data);
    }
}
