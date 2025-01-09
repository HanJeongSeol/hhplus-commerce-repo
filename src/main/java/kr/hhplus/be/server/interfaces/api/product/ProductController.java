package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductListResult;
import kr.hhplus.be.server.application.product.ProductPopularResult;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.interfaces.dto.product.ProductPolularItemResponse;
import kr.hhplus.be.server.interfaces.dto.product.ProductPopularResponse;
import kr.hhplus.be.server.interfaces.dto.product.ProductResponse;
import kr.hhplus.be.server.support.constant.ProductStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<Page<ProductResponse>>> getProducts(
            @Parameter(description = "페이지 정보 (page, size, sort)")
            @PageableDefault(page = 0, size = 10, sort = "productId", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        ProductListResult result = productFacade.getProducts(pageable);
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.PRODUCTS_FOUND,
                new PageImpl<>(
                        result.toResponse(),
                        pageable,
                        result.totalElements()
                )
        ));
    }


    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable Long productId
    ) {
        ProductResult result = productFacade.getProduct(productId);
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.PRODUCT_DETAIL_FOUND,
                result.toResponse()
        ));
    }

    @Operation(summary = "인기 상품 목록 조회", description = "특정 기간의 인기 상품 목록을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<CustomApiResponse<ProductPopularResponse>> getPopularProducts() {
        ProductPopularResult result = productFacade.getPopularProducts();
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.PRODUCTS_FOUND,
                result.toResponse()
        ));
    }
}

