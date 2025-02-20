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
    (1, '항해 후드티', 35000, 100, 'ON_SALE', NOW(), NOW()),
    (2, '항해 티셔츠', 25000, 100, 'ON_SALE', NOW(), NOW()),
    (3, '항해 무지노트', 3000, 100, 'ON_SALE', NOW(), NOW()),
    (4, '항해 볼펜', 1000, 100, 'ON_SALE', NOW(), NOW()),
    (5, '항해 스티커', 2000, 100, 'ON_SALE', NOW(), NOW()),
    (6, '항해 키링', 5000, 100, 'ON_SALE', NOW(), NOW()),
    (7, '항해 마우스패드', 8000, 100, 'ON_SALE', NOW(), NOW()),
    (8, '항해 텀블러', 15000, 100, 'ON_SALE', NOW(), NOW()),
    (9, '항해 백팩', 45000, 100, 'ON_SALE', NOW(), NOW()),
    (10, '항해 모자', 20000, 100, 'ON_SALE', NOW(), NOW());

-- 4. Order 애그리거트
-- 4-1. Order
INSERT INTO order_main (order_id, user_id, total_price, status, created_at, updated_at)
VALUES
    (1, 1, 35000, 'PENDING', NOW(), NOW()),
    (2, 2, 28000, 'COMPLETED', NOW(), NOW()),
    (3, 1, 53000, 'COMPLETED', NOW(), NOW()),
    (4, 2, 25000, 'COMPLETED', NOW(), NOW()),
    (5, 1, 45000, 'COMPLETED', NOW(), NOW());

-- 4-2. OrderLine
INSERT INTO order_line (order_line_id, order_id, product_id, quantity, total_price, created_at, updated_at)
VALUES
    -- 항해 후드티 (3번 주문) - 35000원
    (1, 1, 1, 1, 35000, NOW(), NOW()),
    (2, 3, 1, 1, 35000, NOW(), NOW()),
    (3, 5, 1, 1, 35000, NOW(), NOW()),

    -- 항해 티셔츠 (2번 주문) - 25000원
    (4, 2, 2, 1, 25000, NOW(), NOW()),
    (5, 4, 2, 1, 25000, NOW(), NOW()),

    -- 항해 무지노트 (4번 주문) - 3000원
    (6, 1, 3, 1, 3000, NOW(), NOW()),
    (7, 2, 3, 1, 3000, NOW(), NOW()),
    (8, 3, 3, 1, 3000, NOW(), NOW()),
    (9, 4, 3, 1, 3000, NOW(), NOW()),

    -- 항해 백팩 (1번 주문) - 45000원
    (10, 5, 9, 1, 45000, NOW(), NOW());

-- 5. Coupon
INSERT INTO coupon (coupon_id, name, discount_price, stock, expired_at, created_at, updated_at)
VALUES
    (1, '신규가입 할인 쿠폰', 5000, 10, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
    (2, '여름 시즌 할인 쿠폰', 3000, 20, DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW()),
    (3, '품절 임박 할인 쿠폰', 2000, 30, DATE_ADD(NOW(), INTERVAL 3 DAY), NOW(), NOW());