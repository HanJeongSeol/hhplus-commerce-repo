package kr.hhplus.be.server.support.http;

import kr.hhplus.be.server.support.constant.SuccessCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final int statusCode;
    private final String message;
    private final T data;

    public ApiResponse(SuccessCode successCode, T data) {
        this.success = true;
        this.statusCode = successCode.getHttpStatusCode();
        this.message = successCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> of(SuccessCode successCode, T data){
        return new ApiResponse<>(successCode, data);
    }
}
