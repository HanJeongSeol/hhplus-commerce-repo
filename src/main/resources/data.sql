-- 사용자와 포인트를 함께 추가
INSERT INTO user (user_id, name, point_id, created_at, updated_at)
VALUES (1, '테스트 사용자', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE name = name;

-- 포인트 추가
INSERT INTO point (point_id, balance, created_at, updated_at)
VALUES (1, 1000000, NOW(), NOW())
    ON DUPLICATE KEY UPDATE balance = balance;

-- 쿠폰 추가
INSERT INTO coupon (coupon_id, name, discount_amount, stock, expired_at, status, created_at, updated_at)
VALUES 
    (1, '신규가입 할인 쿠폰', 5000, 100, DATE_ADD(NOW(), INTERVAL 30 DAY), 'AVAILABLE', NOW(), NOW()),
    (2, '여름 맞이 할인 쿠폰', 10000, 50, DATE_ADD(NOW(), INTERVAL 15 DAY), 'AVAILABLE', NOW(), NOW()),
    (3, '첫 구매 할인 쿠폰', 3000, 200, DATE_ADD(NOW(), INTERVAL 60 DAY), 'AVAILABLE', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- 상품 추가
INSERT INTO product (product_id, name, price, stock, status, created_at, updated_at)
VALUES 
    (1, '항해 텀블러', 15000, 100, 'ON_SALE', NOW(), NOW()),
    (2, '항해 후드티', 45000, 50, 'ON_SALE', NOW(), NOW()),
    (3, '항해 티셔츠', 25000, 80, 'ON_SALE', NOW(), NOW()),
    (4, '항해 노트북', 35000, 30, 'ON_SALE', NOW(), NOW()),
    (5, '항해 볼펜', 3000, 200, 'ON_SALE', NOW(), NOW()),
    (6, '항해 마우스패드', 8000, 150, 'ON_SALE', NOW(), NOW()),
    (7, '항해 스티커 세트', 5000, 300, 'ON_SALE', NOW(), NOW()),
    (8, '항해 에코백', 12000, 100, 'ON_SALE', NOW(), NOW()),
    (9, '항해 키링', 7000, 200, 'ON_SALE', NOW(), NOW()),
    (10, '항해 머그컵', 13000, 100, 'ON_SALE', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name; 