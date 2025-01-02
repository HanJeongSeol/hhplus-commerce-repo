package kr.hhplus.be.server.interfaces.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    /**
     * 상품 목록 조회 Mock API
     *
     * {
     *     "data": {
     *         "products": [
     *             {
     *                 "amount": 25000,
     *                 "createdDate": "2025-01-01T00:00:00",
     *                 "productId": 1,
     *                 "stock": 100,
     *                 "productName": "항해 기념품",
     *                 "status": "판매중"
     *             },
     *             {
     *                 "amount": 15000,
     *                 "createdDate": "2025-01-01T00:00:00",
     *                 "productId": 2,
     *                 "stock": 0,
     *                 "productName": "항해 녹차",
     *                 "status": "품절"
     *             }
     *         ]
     *     },
     *     "success": true,
     *     "message": "상품 목록 조회가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @GetMapping
    public Map<String, Object> getProducts() {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> products = new ArrayList<>();

        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", 1);
        product1.put("productName", "항해 기념품");
        product1.put("amount", 25000);
        product1.put("stock", 100);
        product1.put("status", "판매중");
        product1.put("createdDate", "2025-01-01T00:00:00");

        Map<String, Object> product2 = new HashMap<>();
        product2.put("productId", 2);
        product2.put("productName", "항해 녹차");
        product2.put("amount", 15000);
        product2.put("stock", 0);
        product2.put("status", "품절");
        product2.put("createdDate", "2025-01-01T00:00:00");

        products.add(product1);
        products.add(product2);

        Map<String, Object> data = new HashMap<>();
        data.put("products", products);

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "상품 목록 조회가 완료되었습니다.");
        response.put("data", data);

        return response;
    }

    /**
     * 상품 상세 조회 Mock API
     *
     * {
     *     "data": {
     *         "amount": 25000,
     *         "createdDate": "2025-01-01T00:00:00",
     *         "productId": "1",
     *         "modifiedDate": "2025-01-01T10:30:00",
     *         "description": "항해 맞춤 제작 기념품.",
     *         "stock": 100,
     *         "productName": "항해 기념품",
     *         "status": "판매중"
     *     },
     *     "success": true,
     *     "message": "상품 상세 조회가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @GetMapping("/{productId}")
    public Map<String, Object> getProduct(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("productName", "항해 기념품");
        data.put("description", "항해 맞춤 제작 기념품.");
        data.put("amount", 25000);
        data.put("stock", 100);
        data.put("status", "판매중");
        data.put("createdDate", "2025-01-01T00:00:00");
        data.put("modifiedDate", "2025-01-01T10:30:00");

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "상품 상세 조회가 완료되었습니다.");
        response.put("data", data);

        return response;
    }

    /**
     * 인기 상품 조회 Mock API
     * {
     *     "data": {
     *         "standardDate": "2024-03-19T00:00:00",
     *         "products": [
     *             {
     *                 "salesCount": 150,
     *                 "amount": 25000,
     *                 "productId": 1,
     *                 "rank": 1,
     *                 "productName": "항해 기념품",
     *                 "status": "판매중"
     *             },
     *             {
     *                 "salesCount": 120,
     *                 "amount": 20000,
     *                 "productId": 3,
     *                 "rank": 2,
     *                 "productName": "항해 기념품2",
     *                 "status": "판매중"
     *             }
     *         ]
     *     },
     *     "success": true,
     *     "message": "인기 상품 조회가 완료되었습니다.",
     *     "statusCode": 200
     * }
     */
    @GetMapping("/popular")
    public Map<String, Object> getPopularProducts() {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> products = new ArrayList<>();

        Map<String, Object> product1 = new HashMap<>();
        product1.put("rank", 1);
        product1.put("productId", 1);
        product1.put("productName", "항해 기념품");
        product1.put("salesCount", 150);
        product1.put("amount", 25000);
        product1.put("status", "판매중");

        Map<String, Object> product2 = new HashMap<>();
        product2.put("rank", 2);
        product2.put("productId", 3);
        product2.put("productName", "항해 기념품2");
        product2.put("salesCount", 120);
        product2.put("amount", 20000);
        product2.put("status", "판매중");

        products.add(product1);
        products.add(product2);

        Map<String, Object> data = new HashMap<>();
        data.put("standardDate", "2024-03-19T00:00:00");
        data.put("products", products);

        response.put("success", true);
        response.put("statusCode", 200);
        response.put("message", "인기 상품 조회가 완료되었습니다.");
        response.put("data", data);

        return response;
    }
}

