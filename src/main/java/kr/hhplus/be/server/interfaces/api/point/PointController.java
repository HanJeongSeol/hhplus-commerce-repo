package kr.hhplus.be.server.interfaces.api.point;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    /**
     * 포인트 충전 Mock API
     * {
     *   "success": true,
     *   "statusCode": 200,
     *   "message": "포인트 충전이 완료되었습니다.",
     *   "data": {
     *     "userId": 1,
     *     "userName": "홍길동",
     *     "balance": 150000,
     *     "chargedAmount": 50000,
     *     "createdDate": "2025-01-01T10:30:00"
     *   }
     * }
     */
    @PostMapping("/charge")
    public Map<String, Object> chargePoint(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", request.get("userId"));
        data.put("userName", "홍길동");
        data.put("balance", 150000);
        data.put("chargedAmount", request.get("amount"));
        data.put("createdDate", "2025-01-01T10:30:00");

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "포인트 충전이 완료되었습니다.");
        response.put("data", data);

        return response;
    }

    /**
     * 포인트 조회 Mock API
     * {
     *     "data": {
     *         "balance": 150000,
     *         "lastUsedDate": "2025-01-01T15:45:00",
     *         "userName": "홍길동",
     *         "userId": 1,
     *         "lastChargedDate": "2025-01-01T10:30:00"
     *     },
     *     "success": true,
     *     "message": "포인트 조회가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @GetMapping("/{userId}")
    public Map<String, Object> getPointBalance(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("userName", "홍길동");
        data.put("balance", 150000);
        data.put("lastChargedDate", "2025-01-01T10:30:00");
        data.put("lastUsedDate", "2025-01-01T15:45:00");

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "포인트 조회가 완료되었습니다.");
        response.put("data", data);

        return response;
    }
}

