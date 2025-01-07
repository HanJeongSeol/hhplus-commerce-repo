package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.product.ProductPolularItemResponse;
import kr.hhplus.be.server.interfaces.dto.product.ProductPopularResponse;
import kr.hhplus.be.server.interfaces.dto.product.ProductResponse;
import kr.hhplus.be.server.support.constant.ProductStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
@Tag(name ="products", description = "상품 API")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {



    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<ProductResponse>>> getProducts() {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "항해 기념품", 25000L, 100, kr.hhplus.be.server.support.constant.ProductStatus.ON_SALE, LocalDateTime.now(), LocalDateTime.now()),
                new ProductResponse(2L, "항해 녹차", 15000L, 0, kr.hhplus.be.server.support.constant.ProductStatus.SOLD_OUT, LocalDateTime.now(), LocalDateTime.now())
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCTS_FOUND,products));
    }

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<ProductResponse>> getProduct(@PathVariable Long productId) {
        ProductResponse product =  new ProductResponse(1L, "항해 기념품", 25000L, 100, kr.hhplus.be.server.support.constant.ProductStatus.ON_SALE, LocalDateTime.now(), LocalDateTime.now());

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCT_DETAIL_FOUND,product));
    }

    @Operation(summary = "인기 상품 목록 조회", description = "특정 기간의 인기 상품 목록을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<CustomApiResponse<ProductPopularResponse>> getPopularProducts() {
        // Mock 응답 데이터 생성 (실제 구현 시에는 Facade 호출)
        List<ProductPolularItemResponse> popularItems = List.of(
                new ProductPolularItemResponse(
                        1,
                        1L,
                        "항해 기념품",
                        150,
                        25000L,
                        100,
                        ProductStatus.ON_SALE
                ),
                new ProductPolularItemResponse(
                        2,
                        2L,
                        "항해 후드티",
                        120,
                        35000L,
                        50,
                        ProductStatus.ON_SALE
                ),
                new ProductPolularItemResponse(
                        3,
                        3L,
                        "항해 텀블러",
                        100,
                        15000L,
                        0,
                        ProductStatus.SOLD_OUT
                )
        );

        ProductPopularResponse response = new ProductPopularResponse(
                LocalDateTime.now(),
                popularItems
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCTS_FOUND, response));
    }
}

