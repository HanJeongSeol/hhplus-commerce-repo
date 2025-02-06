package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.domain.product.ProductCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCacheRepositoryImpl implements ProductCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_POPULAR_PRODUCTS = "popular:products";
    // 인기상품 목록 캐시 24시간 유효 설정
    private static final long CACHE_TTL = 24 * 60 * 60; // 24시간


    @Override
    public void savePopularProducts(List<ProductResult.ProductPopularResult> products){
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(KEY_POPULAR_PRODUCTS);

            // 빈 리스트라도 저장 (인기상풍 목록 없음 처리)
            if (products.isEmpty()) {
                redisTemplate.opsForValue().set(
                        KEY_POPULAR_PRODUCTS + ":empty",
                        "empty",
                        CACHE_TTL,
                        TimeUnit.SECONDS
                );
                log.info("인기상품 목록이 없습니다.");s
                return;
            }

            // 데이터가 있는 경우 정상 저장
            for (int i = 0; i < products.size(); i++) {
                redisTemplate.opsForZSet().add(
                        KEY_POPULAR_PRODUCTS,
                        products.get(i),
                        products.size() - i
                );
            }

            // TTL 설정 -> 24시간
            redisTemplate.expire(KEY_POPULAR_PRODUCTS, CACHE_TTL, TimeUnit.SECONDS);
            log.info("인기상품 목록 캐시 크기: {}", products.size());

        } catch (Exception e) {
            log.error("인기상품 목록 캐시 저장 실패", e);
        }
    }
    @Override
    public List<ProductResult.ProductPopularResult> getPopularProducts(){
        try {
            // 빈 리스트 표시 확인
            Boolean isEmpty = redisTemplate.hasKey(KEY_POPULAR_PRODUCTS + ":empty");
            if (Boolean.TRUE.equals(isEmpty)) {
                log.info("인기상품 목록 비어있음.");
                return Collections.emptyList();
            }

            // 정상 데이터 조회
            Set<Object> products = redisTemplate.opsForZSet()
                    .reverseRange(KEY_POPULAR_PRODUCTS, 0, 4);  // top 5

            if (products == null || products.isEmpty()) {
                return Collections.emptyList();
            }

            return products.stream()
                    .map(p -> (ProductResult.ProductPopularResult) p)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("인기상품 캐시 조회 실패", e);
            return Collections.emptyList();
        }
    }
}
