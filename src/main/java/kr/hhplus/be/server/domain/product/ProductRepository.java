package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    List<Product> findAll();
    Optional<Product> findById(Long productId);
    Optional<Product> findByIdWithLock(Long productId);
    Page<Product> findAll(Pageable pageable);
    List<ProductInfo.ProductPopularQueryDto> findPopularProducts();
}
