package kr.hhplus.be.server.interfaces.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    /**
     * 결제 처리 Mock API
     * {
     *     "data": {
     *         "orderId": 1,
     *         "paymentId": 1,
     *         "remainingPoints": 105000,
     *         "paymentDate": "2025-01-01T10:30:00",
     *         "paymentAmount": 45000,
     *         "paymentStatus": "결제완료"
     *     },
     *     "success": true,
     *     "message": "결제가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @PostMapping
    public Map<String, Object> processPayment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", 1);
        data.put("orderId", request.get("orderId"));
        data.put("paymentAmount", 45000);
        data.put("paymentStatus", "결제완료");
        data.put("paymentDate", "2025-01-01T10:30:00");
        data.put("remainingPoints", 105000);

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "결제가 완료되었습니다.");
        response.put("data", data);

        return response;
    }
}
