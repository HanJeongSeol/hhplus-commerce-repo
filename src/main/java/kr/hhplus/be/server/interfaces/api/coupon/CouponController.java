package kr.hhplus.be.server.interfaces.api.coupon;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    /**
     * 쿠폰 발급 Mock API
     *{
     *     "data": {
     *         "expiredAt": "2025-01-02T23:59:59",
     *         "couponName": "신규 가입 할인 쿠폰",
     *         "discountAmount": 5000,
     *         "userCouponId": 1,
     *         "issueDate": "2025-01-01T10:30:00",
     *         "status": "미사용"
     *     },
     *     "success": true,
     *     "message": "쿠폰이 발급되었습니다.",
     *     "statusCode": 200
     * }
     */
    @PostMapping("/issue")
    public Map<String, Object> issueCoupon(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("userCouponId", 1);
        data.put("couponName", "신규 가입 할인 쿠폰");
        data.put("discountAmount", 5000);
        data.put("expiredAt", "2025-01-02T23:59:59");
        data.put("status", "미사용");
        data.put("issueDate", "2025-01-01T10:30:00");

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "쿠폰이 발급되었습니다.");
        response.put("data", data);

        return response;
    }

    /**
     * 쿠폰 발급 내역 조회 Mock API
     * {
     *     "data": {
     *         "coupons": [
     *             {
     *                 "expiredAt": "2025-01-02T23:59:59",
     *                 "couponName": "신규 가입 할인 쿠폰",
     *                 "discountAmount": 5000,
     *                 "userCouponId": 1,
     *                 "issueDate": "2025-01-01T10:30:00",
     *                 "status": "미사용"
     *             },
     *             {
     *                 "expiredAt": "2025-01-01T23:59:59",
     *                 "couponName": "추가 할인 쿠폰",
     *                 "discountAmount": 3000,
     *                 "usedDate": "2024-12-25T14:20:00",
     *                 "userCouponId": 2,
     *                 "issueDate": "2024-12-24T00:00:00",
     *                 "status": "사용완료"
     *             }
     *         ]
     *     },
     *     "success": true,
     *     "message": "보유 쿠폰 조회가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserCoupons(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> coupons = new ArrayList<>();

        Map<String, Object> coupon1 = new HashMap<>();
        coupon1.put("userCouponId", 1);
        coupon1.put("couponName", "신규 가입 할인 쿠폰");
        coupon1.put("discountAmount", 5000);
        coupon1.put("status", "미사용");
        coupon1.put("expiredAt", "2025-01-02T23:59:59");
        coupon1.put("issueDate", "2025-01-01T10:30:00");

        Map<String, Object> coupon2 = new HashMap<>();
        coupon2.put("userCouponId", 2);
        coupon2.put("couponName", "추가 할인 쿠폰");
        coupon2.put("discountAmount", 3000);
        coupon2.put("status", "사용완료");
        coupon2.put("expiredAt", "2025-01-01T23:59:59");
        coupon2.put("issueDate", "2024-12-24T00:00:00");
        coupon2.put("usedDate", "2024-12-25T14:20:00");

        coupons.add(coupon1);
        coupons.add(coupon2);

        Map<String, Object> data = new HashMap<>();
        data.put("coupons", coupons);

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "보유 쿠폰 조회가 완료되었습니다.");
        response.put("data", data);

        return response;
    }
}
