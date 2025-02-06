package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.request.ProductCommand;
import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    /**
     * 페이징 처리된 상품 목록 리스트
     */
    public Page<ProductResult.ProductInfoResult> getProductPage(ProductCommand.GetProductList command) {
        return productService.getAllProductPage(command.page(), command.size())
                .map(ProductResult.ProductInfoResult::from);
    }

    /**
     * 단일 상품 상세 정보 조히
     */
    public ProductResult.ProductInfoResult getProduct(ProductCommand.GetProduct command) {
        return ProductResult.ProductInfoResult
                .from(productService.getProductByIdWithLock(command.productId()));
    }

    public List<ProductResult.ProductPopularResult> getProductPopularList() {
        return productService.getPopularProducts().stream()
                .map(ProductResult.ProductPopularResult::from)
                .collect(Collectors.toList());
    }

    /**
     * 레디스 캐시를 활용한 인기 상품 조회
     */
    public List<ProductResult.ProductPopularResult> getProductPopularListByRedis() {
        return productService.getPopularProductsByRedis();

    }

}
