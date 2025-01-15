package kr.hhplus.be.server.application.product.request;

import kr.hhplus.be.server.interfaces.dto.product.request.ProductRequest;

public class ProductCommand {
    public record GetProductList(
            int page,
            int size
    ) {
        public static GetProductList from(ProductRequest.ProductInfoRequest request){
            return new GetProductList(
                    request.page(),
                    request.size()
            );
        }
    }

    public record GetProduct(
            Long productId
    ) {
        public static GetProduct from(ProductRequest.ProductDetailInfoRequest request){
            return new GetProduct(
                    request.productId()
            );
        }
    }
}
