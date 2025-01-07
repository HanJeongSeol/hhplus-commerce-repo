package kr.hhplus.be.server.support.constant;


import io.swagger.v3.oas.annotations.media.Schema;

public enum ProductStatus {
    @Schema(description = "판매 중")
    ON_SALE,    // 판매 중
    @Schema(description = "품절")
    SOLD_OUT    // 품절
}
