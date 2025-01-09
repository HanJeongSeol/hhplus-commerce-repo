package kr.hhplus.be.server.support.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // 공통 성공 코드
    OK(HttpStatus.OK, "정상 처리되었습니다"),
    CREATED(HttpStatus.CREATED, "생성되었습니다"),

    // 포인트 성공 코드
    POINT_CHARGED(HttpStatus.OK, "포인트가 충전되었습니다"),
    POINT_BALANCE_CHECKED(HttpStatus.OK, "포인트 잔액이 조회되었습니다"),
    POINT_USED(HttpStatus.OK, "포인트가 사용되었습니다"),

    // 상품 성공 코드
    PRODUCTS_FOUND(HttpStatus.OK, "상품 목록이 조회되었습니다"),
    PRODUCT_DETAIL_FOUND(HttpStatus.OK, "상품 상세정보가 조회되었습니다"),
    POPULAR_PRODUCTS_FOUND(HttpStatus.OK, "인기 상품 목록이 조회되었습니다"),

    // 쿠폰 성공 코드
    COUPON_ISSUED(HttpStatus.CREATED, "쿠폰이 발급되었습니다"),
    COUPONS_FOUND(HttpStatus.OK, "쿠폰 목록이 조회되었습니다"),
    COUPON_DETAIL_FOUND(HttpStatus.OK, "쿠폰 상세정보가 조회되었습니다"),
    COUPON_USED(HttpStatus.OK, "쿠폰이 사용되었습니다"),

    // 주문 성공 코드
    ORDER_CREATED(HttpStatus.CREATED, "주문이 생성되었습니다"),
    ORDER_FOUND(HttpStatus.OK, "주문 정보가 조회되었습니다"),
    ORDERS_FOUND(HttpStatus.OK, "주문 목록이 조회되었습니다"),
    ORDER_CANCELLED(HttpStatus.OK, "주문이 취소되었습니다"),

    // 결제 성공 코드
    PAYMENT_COMPLETED(HttpStatus.OK, "결제가 완료되었습니다"),
    PAYMENT_FOUND(HttpStatus.OK, "결제 정보가 조회되었습니다"),
    PAYMENTS_FOUND(HttpStatus.OK, "결제 목록이 조회되었습니다"),
    PAYMENT_CANCELLED(HttpStatus.OK, "결제가 취소되었습니다");


    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}
