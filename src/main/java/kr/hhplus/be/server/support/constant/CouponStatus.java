package kr.hhplus.be.server.support.constant;

import io.swagger.v3.oas.annotations.media.Schema;

public enum CouponStatus {
    @Schema(description = "사용 가능")
    ACTIVE,
    @Schema(description = "사용 완료")
    USED,
    @Schema(description = "만료")
    EXPIRED
}
