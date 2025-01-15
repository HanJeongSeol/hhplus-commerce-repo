package kr.hhplus.be.server.application.product.response;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.time.LocalDateTime;

public class ProductResult {
    public record ProductInfoResult(
            Long productId,
            String productName,
            Long price,
            Integer stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static ProductInfoResult from(ProductInfo.ProductDetail productList){
            return new ProductInfoResult(
                    productList.productId(),
                    productList.productName(),
                    productList.price(),
                    productList.stock(),
                    productList.status(),
                    productList.createdAt(),
                    productList.updatedAt()
            );
        }
    }

    public record ProductPopularResult(
            Integer rank,
            Long productId,
            String productName,
            Long salesCount,
            Long price,
            Integer stock,
            ProductStatus status
    ) {
        public static ProductPopularResult from(ProductInfo.ProductPopularList productList){
            return new ProductPopularResult(
                    productList.rank(),
                    productList.productId(),
                    productList.productName(),
                    productList.salesCount(),
                    productList.price(),
                    productList.stock(),
                    productList.status()
            );
        }
    }
}
