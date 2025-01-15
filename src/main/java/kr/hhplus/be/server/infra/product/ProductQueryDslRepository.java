package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.List;

public interface ProductQueryDslRepository {
    List<ProductInfo.ProductPopularQueryDto> findPopularProducts();
}
