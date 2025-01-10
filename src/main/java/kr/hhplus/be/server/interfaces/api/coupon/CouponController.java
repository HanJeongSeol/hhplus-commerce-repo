package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.dto.coupon.UserCouponResponse;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name="coupons", description = "쿠폰 API")
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @Operation(summary = "쿠폰 발급", description = "사용자 쿠폰 발급")
    @PostMapping("/issue")
    public ResponseEntity<CustomApiResponse<CouponIssueResponse>> issueCoupon(@RequestBody CouponIssueRequest request) {
        CouponIssueResponse response = new CouponIssueResponse(
                1L,
                "신규 가입 할인 쿠폰",
                5000L,
                CouponStatus.ACTIVE,
                LocalDateTime.parse("2025-01-01T10:30:00"),
                LocalDateTime.parse("2025-01-02T23:59:59")
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPON_ISSUED, response));
    }
    @Operation(summary = "쿠폰 조회", description = "사용자 쿠폰 목록 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CustomApiResponse<UserCouponResponse>> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable String userId) {
        List<UserCouponResponse.CouponItem> coupons = List.of(
                new UserCouponResponse.CouponItem(
                        1L, "신규 가입 할인 쿠폰", 5000L,
                        CouponStatus.ACTIVE, LocalDateTime.parse("2025-01-01T10:30:00"),
                        LocalDateTime.parse("2025-01-02T23:59:59"), null
                ),
                new UserCouponResponse.CouponItem(
                        2L, "추가 할인 쿠폰", 3000L,
                        CouponStatus.USED, LocalDateTime.parse("2024-12-24T00:00:00"),
                        LocalDateTime.parse("2025-01-01T23:59:59"),
                        LocalDateTime.parse("2024-12-25T14:20:00")
                )
        );

        UserCouponResponse response = new UserCouponResponse(coupons);

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPONS_FOUND, response));
    }
}
