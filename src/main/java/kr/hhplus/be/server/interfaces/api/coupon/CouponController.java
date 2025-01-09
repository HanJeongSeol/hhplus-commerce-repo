package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.CouponIssueCommand;
import kr.hhplus.be.server.application.coupon.CouponIssueResult;
import kr.hhplus.be.server.application.coupon.UserCouponListResult;
import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.dto.coupon.UserCouponResponse;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name="coupons", description = "쿠폰 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "쿠폰 발급", description = "사용자 쿠폰 발급")
    @PostMapping("/issue")
    public ResponseEntity<CustomApiResponse<CouponIssueResponse>> issueCoupon(@RequestBody CouponIssueRequest request) {

        CouponIssueResult issueResult = couponFacade.issueCoupon(
                CouponIssueCommand.from(request)
        );
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPON_ISSUED, issueResult.toResponse()));
    }

    @Operation(summary = "쿠폰 조회", description = "사용자 쿠폰 목록 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CustomApiResponse<UserCouponResponse>> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId) {
        UserCouponListResult couponListResult = couponFacade.getUserCoupons(userId);

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPONS_FOUND, couponListResult.toResponse()));
    }

}
