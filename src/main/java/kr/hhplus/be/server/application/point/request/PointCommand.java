package kr.hhplus.be.server.application.point.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.interfaces.dto.point.request.PointChargeRequest;

public class PointCommand{
    @Schema(description = "포인트 충전 요청")
    public record PointCharge(
            Long userId,
            Long amount
    ) {
        public static PointCharge from(PointChargeRequest request){
            return new PointCharge(
                    request.userId(),
                    request.amount()
            );
        }
    }

    @Schema(description = "포인트 조회 요청")
    public record PointBalance(
            Long userId
    ){
        public static PointBalance from(Long userId){
            return new PointBalance(
                    userId
            );
        }
    }

}