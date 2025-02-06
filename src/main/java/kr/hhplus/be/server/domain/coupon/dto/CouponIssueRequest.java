package kr.hhplus.be.server.domain.coupon.dto;

import lombok.Value;

@Value
public class CouponIssueRequest{
    Long couponId;
    Long userId;
    long timestamp;
}
