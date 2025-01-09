package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    @Transactional
    public Page<Product> getAllProductPage(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);
        if(products.isEmpty()){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product getProductByIdWithLock(Long productId){
        return productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void decreaseProductStock(Long productId, int quantity){
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.decreaseStock(quantity);

        productRepository.save(product);

    }

}
