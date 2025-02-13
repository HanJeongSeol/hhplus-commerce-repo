package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.request.ProductCommand;
import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.interfaces.dto.product.request.ProductRequest;
import kr.hhplus.be.server.interfaces.dto.product.response.ProductResponse;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name ="products", description = "상품 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductFacade productFacade;

    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<Page<ProductResponse.ProductInfoResponse>>> getProducts(
            ProductRequest.ProductInfoRequest request
    ) {
        Page<ProductResult.ProductInfoResult> result = productFacade.getProductPage(ProductCommand.GetProductList.from(request));
        Page<ProductResponse.ProductInfoResponse> response = result.map(ProductResponse.ProductInfoResponse::toResponse);
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCTS_FOUND,response));
    }

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<ProductResponse.ProductInfoResponse>> getProduct(ProductRequest.ProductDetailInfoRequest request) {
        ProductResult.ProductInfoResult result = productFacade.getProduct(ProductCommand.GetProduct.from(request));
        ProductResponse.ProductInfoResponse response = ProductResponse.ProductInfoResponse.toResponse(result);
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCT_DETAIL_FOUND,response));
    }

    @Operation(summary = "인기 상품 목록 조회", description = "특정 기간의 인기 상품 목록을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<CustomApiResponse<List<ProductResponse.ProductPopularResponse>>> getPopularProducts() {

        List<ProductResult.ProductPopularResult> result = productFacade.getProductPopularList();
        List<ProductResponse.ProductPopularResponse> response = result.stream()
                .map(ProductResponse.ProductPopularResponse::toResponse)
                .toList();

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCTS_FOUND, response));
    }

    @Operation(summary = "인기 상품 목록 조회_레디스 캐시 활용", description = "특정 기간의 인기 상품 목록을 조회합니다.")
    @GetMapping("/redis/popular")
    public ResponseEntity<CustomApiResponse<List<ProductResponse.ProductPopularResponse>>> getPopularProductsByRedis() {
        List<ProductResult.ProductPopularResult> results = productFacade.getProductPopularListByRedis();
        List<ProductResponse.ProductPopularResponse> response = results.stream()
                .map(ProductResponse.ProductPopularResponse::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PRODUCTS_FOUND, response));
    }
}

