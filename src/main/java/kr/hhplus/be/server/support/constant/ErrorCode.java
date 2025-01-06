package kr.hhplus.be.server.support.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {

    // 공통 에러 코드
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,  "서버 오류가 발생했습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN,  "접근 권한이 없습니다"),

    // 포인트 에러 코드
    USER_POINT_NOT_FOUND(HttpStatus.NOT_FOUND,  "사용자의 포인트 정보를 찾을 수 없습니다"),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST,  "유효하지 않은 포인트 금액입니다"),
    POINT_CHARGE_FAILED(HttpStatus.BAD_REQUEST,  "포인트 충전에 실패했습니다"),
    INSUFFICIENT_POINT_BALANCE(HttpStatus.BAD_REQUEST,  "포인트 잔액이 부족합니다"),
    POINT_EXCEED_MAX_VALUE(HttpStatus.BAD_REQUEST, "최대 충전 가능 금액을 초과했습니다"),

    // 상품 에러 코드
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST,  "상품이 품절되었습니다"),

    // 쿠폰 에러 코드
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다"),
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST,  "이미 발급된 쿠폰입니다"),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다"),
    COUPON_OUT_OF_STOCK(HttpStatus.BAD_REQUEST,  "쿠폰이 모두 소진되었습니다"),
    COUPON_NOT_AVAILABLE(HttpStatus.BAD_REQUEST,  "사용 불가능한 쿠폰입니다"),

    // 주문 에러 코드
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다"),
    INVALID_ORDER_QUANTITY(HttpStatus.BAD_REQUEST,  "유효하지 않은 주문 수량입니다"),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST,  "이미 취소된 주문입니다"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태입니다"),

    // 결제 에러 코드
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND,  "결제 정보를 찾을 수 없습니다"),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST,  "이미 결제가 완료된 주문입니다"),
    PAYMENT_CANCELLED(HttpStatus.BAD_REQUEST, "취소된 결제입니다");




    private final HttpStatus httpStatus;
    private final String message;
    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
