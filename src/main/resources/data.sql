-- 1. User 애그리거트
INSERT INTO user (user_id, name, created_at, updated_at)
VALUES
    (1, '설한정', NOW(), NOW()),
    (2, '신우진', NOW(), NOW());

-- 2. Point 애그리거트
INSERT INTO point (point_id, user_id, balance, created_at, updated_at)
VALUES
    (1, 1, 100000, NOW(), NOW()),
    (2, 2, 200000, NOW(), NOW());

-- 3. Product 애그리거트
INSERT INTO product (product_id, name, price, stock, status, created_at, updated_at)
VALUES
    (1, '항해 후드티', 1, 10000, 'ON_SALE', NOW(), NOW()),
    (2, '항해 티셔츠', 25000, 100, 'ON_SALE', NOW(), NOW()),
    (3, '항해 무지노트', 3000, 100, 'ON_SALE', NOW(), NOW()),
    (4, '항해 볼펜', 1000, 100, 'ON_SALE', NOW(), NOW()),
    (5, '항해 스티커', 2000, 100, 'ON_SALE', NOW(), NOW()),
    (6, '항해 키링', 5000, 100, 'ON_SALE', NOW(), NOW()),
    (7, '항해 마우스패드', 8000, 100, 'ON_SALE', NOW(), NOW()),
    (8, '항해 텀블러', 15000, 100, 'ON_SALE', NOW(), NOW()),
    (9, '항해 백팩', 45000, 100, 'ON_SALE', NOW(), NOW()),
    (10, '항해 모자', 1, 100, 'ON_SALE', NOW(), NOW());

-- 4. Coupon 애그리거트
-- 4-1. Coupon
INSERT INTO coupon (coupon_id, name, discount_price, stock, expired_at, created_at, updated_at)
VALUES
    (1, '신규가입 할인쿠폰', 5000, 100, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
    (2, '여름방학 특별할인', 10000, 50, DATE_ADD(NOW(), INTERVAL 14 DAY), NOW(), NOW()),
    (3, 'VIP 전용쿠폰', 20000, 10, DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW());