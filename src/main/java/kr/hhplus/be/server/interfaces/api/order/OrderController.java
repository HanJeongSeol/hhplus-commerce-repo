package kr.hhplus.be.server.interfaces.api.order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    /**
     * 주문 생성 Mock API
     * {
     *     "data": {
     *         "totalAmount": 50000,
     *         "orderDetails": [
     *             {
     *                 "unitPrice": 25000,
     *                 "quantity": 2,
     *                 "totalPrice": 50000,
     *                 "productName": "항해 기념품"
     *             }
     *         ],
     *         "orderId": 1,
     *         "appliedCoupon": {
     *             "couponName": "신규 가입 할인 쿠폰",
     *             "discountAmount": 5000
     *         },
     *         "discountAmount": 5000,
     *         "orderDate": "2025-01-01T10:30:00",
     *         "paymentAmount": 45000
     *     },
     *     "success": true,
     *     "message": "주문서가 생성되었습니다.",
     *     "statusCode": 201
     * }
     */
    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> orderDetail = new HashMap<>();
        orderDetail.put("productName", "항해 기념품");
        orderDetail.put("quantity", request.get("quantity"));
        orderDetail.put("unitPrice", 25000);
        orderDetail.put("totalPrice", 50000);

        Map<String, Object> couponInfo = new HashMap<>();
        couponInfo.put("couponName", "신규 가입 할인 쿠폰");
        couponInfo.put("discountAmount", 5000);

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", 1);
        data.put("orderDate", "2025-01-01T10:30:00");
        data.put("totalAmount", 50000);
        data.put("discountAmount", 5000);
        data.put("paymentAmount", 45000);
        data.put("orderDetails", List.of(orderDetail));
        data.put("appliedCoupon", couponInfo);

        response.put("success", true);
        response.put("statusCode", 201);
        response.put("message", "주문서가 생성되었습니다.");
        response.put("data", data);

        return response;
    }
}
