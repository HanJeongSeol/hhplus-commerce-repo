package kr.hhplus.be.server.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.point.PointBalanceResponse;
import kr.hhplus.be.server.interfaces.dto.point.PointChargeRequest;
import kr.hhplus.be.server.interfaces.dto.point.PointChargeResponse;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "points", description = "포인트 API")
@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전하는 API")
    @PostMapping("/charge")
    public ResponseEntity<CustomApiResponse<PointChargeResponse>> chargePoint(@Parameter(description = "충전 요청 데이터", required = true) @RequestBody PointChargeRequest request) {
        PointChargeResponse response = new PointChargeResponse(
                request.userId(),
                "설한정",
                150000L,
                request.amount(),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.POINT_CHARGED, response));
    }

    @Operation(summary = "포인트 조회", description = "사용자의 포인트 잔액을 조회하는 API")
    @GetMapping("/{userId}")
    public ResponseEntity<CustomApiResponse<PointBalanceResponse>> getPointBalance(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId) {

        // 임시 응답 데이터 (실제 DB 조회로 교체 필요)
        PointBalanceResponse response = new PointBalanceResponse(
                userId,
                "설한정",
                150000L
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.POINT_BALANCE_CHECKED, response));
    }
}

