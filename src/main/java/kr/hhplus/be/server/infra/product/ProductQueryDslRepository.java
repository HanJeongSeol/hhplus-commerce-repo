package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductPopularQueryDto;

import java.util.List;

public interface ProductQueryDslRepository {
    List<ProductPopularQueryDto> findPopularProducts();
}
