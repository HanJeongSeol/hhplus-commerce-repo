package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.time.LocalDateTime;

public class ProductInfo {
    public record ProductDetail(
            Long productId,
            String productName,
            Long price,
            Integer stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        public static ProductDetail from(Product product){
            return new ProductDetail(
                    product.getProductId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getStatus(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()
            );
        }
    }

    public record ProductPopularQueryDto(
            Long productId,
            String productName,
            Long salesCount,
            Long price,
            Integer stock,
            ProductStatus status
    ) {}
    public record ProductPopularList(
            Integer rank,
            Long productId,
            String productName,
            Long salesCount,
            Long price,
            Integer stock,
            ProductStatus status
    ) {
        public static ProductPopularList from(ProductPopularQueryDto queryDto, int rank){
            return new ProductPopularList(
                    rank,
                    queryDto.productId(),
                    queryDto.productName(),
                    queryDto.salesCount(),
                    queryDto.price(),
                    queryDto.stock(),
                    queryDto.status()
            );
        }
    }

}
