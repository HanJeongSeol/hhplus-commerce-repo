package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.request.CouponCommand;
import kr.hhplus.be.server.application.coupon.response.CouponResult;
import kr.hhplus.be.server.interfaces.dto.coupon.request.CouponRequest;
import kr.hhplus.be.server.interfaces.dto.coupon.response.CouponResponse;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="coupons", description = "쿠폰 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "쿠폰 발급", description = "사용자 쿠폰 발급")
    @PostMapping("/issue")
    public ResponseEntity<CustomApiResponse<CouponResponse.IssueResponse>> issueCoupon(@RequestBody CouponRequest.IssueRequest request) {
        CouponResult.IssueResult result = couponFacade.issueCoupon(CouponCommand.IssueCoupon.from(request));
        CouponResponse.IssueResponse response = CouponResponse.IssueResponse.toResponse(result);
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPON_ISSUED, response));
    }

    @Operation(summary = "쿠폰 조회", description = "사용자 쿠폰 목록 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CustomApiResponse<List<CouponResponse.UserCouponResponse>>> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId) {

        CouponRequest.UserCouponInfoRequest request = new CouponRequest.UserCouponInfoRequest(userId);

        // Facade 호출
        List<CouponResult.UserCouponResult> results = couponFacade.getUserCouponList(CouponCommand.UserCouponInfo.from(request));

        List<CouponResponse.UserCouponResponse >response = results.stream()
                .map(CouponResponse.UserCouponResponse::toResponse)
                .toList();
        System.out.println(response);

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.COUPONS_FOUND, response));
    }

    @Operation(summary = "쿠폰 발급 요청", description = "비동기 쿠폰 발급 요청")
    @PostMapping("/issue/async")
    public ResponseEntity<CustomApiResponse<CouponResponse.IssueRequestResponse>> requestIssueCoupon(
            @RequestBody CouponRequest.IssueRequest request) {
        CouponResult.IssueRequestResult result =
                couponFacade.requestCouponIssue(CouponCommand.IssueCoupon.from(request));
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.COUPON_ISSUED,
                CouponResponse.IssueRequestResponse.toResponse(result)
        ));
    }
}
