package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.List;

public interface ProductCacheRepository {
    void savePopularProducts(List<ProductResult.ProductPopularResult> products);

    List<ProductResult.ProductPopularResult> getPopularProducts();
}
