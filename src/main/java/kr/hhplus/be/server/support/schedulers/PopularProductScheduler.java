package kr.hhplus.be.server.support.schedulers;

import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PopularProductScheduler {
    private final ProductService productService;

    // 매일 자정에 인기 상품 목록 갱신 - 테스트를 위해 10초로 설정
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedDelay = 10000)
    public void updatePopularProducts(){
        productService.getPopularProductsByRedis();
    }
}
