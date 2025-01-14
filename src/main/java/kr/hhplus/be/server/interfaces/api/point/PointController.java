package kr.hhplus.be.server.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.request.PointCommand;
import kr.hhplus.be.server.interfaces.dto.point.PointBalanceResponse;
import kr.hhplus.be.server.interfaces.dto.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.dto.point.response.PointResponse;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "points", description = "포인트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointFacade pointFacade;

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전하는 API")
    @PostMapping("/charge")
    public ResponseEntity<CustomApiResponse<PointResponse.PointChargeResponse>> chargePoint(@Parameter(description = "충전 요청 데이터", required = true) @RequestBody PointChargeRequest request) {

        PointResponse.PointChargeResponse response = PointResponse.PointChargeResponse.toResponse(
                pointFacade.chargePoint(PointCommand.PointCharge.from(request))
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.POINT_CHARGED, response));
    }

    @Operation(summary = "포인트 조회", description = "사용자의 포인트 잔액을 조회하는 API")
    @GetMapping("/{userId}")
    public ResponseEntity<CustomApiResponse<PointResponse.PointBalanceResponse>> getPointBalance(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId) {

        System.out.println("controller useID : " + userId);
        PointResponse.PointBalanceResponse response = PointResponse.PointBalanceResponse.toResponse(
                pointFacade.getPointBalance(PointCommand.PointBalance.from(userId))
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.POINT_BALANCE_CHECKED, response));
    }
}

