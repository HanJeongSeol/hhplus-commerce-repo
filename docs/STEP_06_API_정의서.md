## e-커머스 프로젝트 API 정의서

---

## 1. 포인트 관리 API

### 1.1 포인트 충전
- **Endpoint**: `POST /api/v1/points/charge`
- **Description**: 사용자의 포인트를 충전합니다.
- **Request**:
  ```json
  {
    "userId": 1,
    "amount": 50000
  }
  ```
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "포인트 충전이 완료되었습니다.",
    "data": {
      "userId": 1,
      "userName": "홍길동",
      "balance": 150000,
      "chargedAmount": 50000,
      "createdDate": "2025-01-01T10:30:00"
    }
  }
  ```
- **Error Response**:
  ```json
  {
    "success": false,
    "statusCode": 400,
    "message": "충전 금액은 1,000원 이상이어야 합니다.",
    "data": null
  }
  ```

### 1.2 포인트 조회
- **Endpoint**: `GET /api/v1/points/{userId}`
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "포인트 조회가 완료되었습니다.",
    "data": {
      "userId": 1,
      "userName": "홍길동",
      "balance": 150000,
      "lastChargedDate": "2025-01-01T10:30:00",
      "lastUsedDate": "2025-01-01T15:45:00"
    }
  }
  ```

## 2. 상품 관리 API

### 2.1 상품 목록 조회
- **Endpoint**: `GET /api/v1/products`
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "상품 목록 조회가 완료되었습니다.",
    "data": {
      "products": [
        {
          "productId": 1,
          "productName": "항해 기념품",
          "amount": 25000,
          "stock": 100,
          "status": "판매중",
          "createdDate": "2025-01-01T00:00:00"
        },
        {
          "productId": 2,
          "productName": "항해 녹차",
          "amount": 15000,
          "stock": 0,
          "status": "품절",
          "createdDate": "2025-01-01T00:00:00"
        }
      ]
    }
  }
  ```

### 2.2 상품 상세 조회
- **Endpoint**: `GET /api/v1/products/{productId}`
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "상품 상세 조회가 완료되었습니다.",
    "data": {
      "productId": 1,
      "productName": "항해 기념품",
      "description": "항해 맞춤 제작 기념품.",
      "amount": 25000,
      "stock": 100,
      "status": "판매중",
      "createdDate": "2025-01-01T00:00:00",
      "modifiedDate": "2025-01-01T10:30:00"
    }
  }
  ```

## 3. 쿠폰 관리 API

### 3.1 쿠폰 발급
- **Endpoint**: `POST /api/v1/coupons/issue`
- **Request**:
  ```json
  {
    "userId": 1,
    "couponId": 1
  }
  ```
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "쿠폰이 발급되었습니다.",
    "data": {
      "userCouponId": 1,
      "couponName": "신규 가입 할인 쿠폰",
      "discountAmount": 5000,
      "expiredAt": "2025-01-02T23:59:59",
      "status": "미사용",
      "issueDate": "2025-01-01T10:30:00"
    }
  }
  ```

### 3.2 보유 쿠폰 조회
- **Endpoint**: `GET /api/v1/coupons/users/{userId}`
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "보유 쿠폰 조회가 완료되었습니다.",
    "data": {
      "coupons": [
        {
          "userCouponId": 1,
          "couponName": "신규 가입 할인 쿠폰",
          "discountAmount": 5000,
          "status": "미사용",
          "expiredAt": "2025-01-02T23:59:59",
          "issueDate": "2025-01-01T10:30:00"
        },
        {
          "userCouponId": 2,
          "couponName": "추가 할인 쿠폰",
          "discountAmount": 3000,
          "status": "사용완료",
          "expiredAt": "2025-01-01T23:59:59",
          "issueDate": "2024-12-24T00:00:00",
          "usedDate": "2024-12-25T14:20:00"
        }
      ]
    }
  }
  ```

## 4. 주문/결제 API

### 4.1 주문서 생성
- **Endpoint**: `POST /api/v1/orders`
- **Request**:
```json
  {
    "userId": 1,
    "orderDetails": [
      {
      "productId": 1,
      "quantity": 2
      },
      {
      "productId": 3,
      "quantity": 1
      }
    ],
    "userCouponId": 1
  }
  ```
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 201,
    "message": "주문서가 생성되었습니다.",
    "data": {
      "orderId": 1,
      "orderDate": "2025-01-01T10:30:00",
      "totalAmount": 70000,
      "discountAmount": 5000,
      "paymentAmount": 65000,
      "orderDetails": [
        {
          "productName": "항해 기념품",
          "quantity": 2,
          "unitPrice": 25000,
          "totalPrice": 50000
        },
        {
          "productName": "항해 녹차",
          "quantity": 1,
          "unitPrice": 20000,
          "totalPrice": 20000
        }
      ],
      "appliedCoupon": {
        "couponName": "신규 가입 할인 쿠폰",
        "discountAmount": 5000
      }
    }
  }
  ```

### 4.2 결제 처리
- **Endpoint**: `POST /api/v1/payments`
- **Request**:
  ```json
  {
    "userId": 1,
    "orderId": 1
  }
  ```
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 201,
    "message": "결제가 완료되었습니다.",
    "data": {
      "paymentId": 1,
      "orderId": 1,
      "paymentAmount": 45000,
      "paymentStatus": "결제완료",
      "paymentDate": "2025-01-01T10:30:00",
      "remainingPoints": 105000
    }
  }
  ```

## 5. 인기 상품 API

### 5.1 인기 상품 목록 조회
- **Endpoint**: `GET /api/v1/products/popular`
- **Success Response**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "message": "인기 상품 조회가 완료되었습니다.",
    "data": {
      "standardDate": "2024-03-19T00:00:00",
      "products": [
        {
          "rank": 1,
          "productId": 1,
          "productName": "항해 기념품",
          "salesCount": 150,
          "amount": 25000,
          "status": "판매중"
        },
        {
          "rank": 2,
          "productId": 3,
          "productName": "항해 기념품2",
          "salesCount": 120,
          "amount": 20000,
          "status": "판매중"
        }
      ]
    }
  }
  ```

---

## 6.참고 사항
- 각 API의 **Error Response**는 다음과 같은 형식을 따릅니다:
```json
{
  "success": false,
  "statusCode": 400,
  "message": "요청 처리 중 오류가 발생했습니다.",
  "data": null
}
```
