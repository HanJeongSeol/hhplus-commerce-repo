package kr.hhplus.be.server.config.redis;

import kr.hhplus.be.server.config.redis.annotation.RedissonLock;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@Order(1) // @Transactional보다 우선 실행되어야 한다. 락 획득 -> 트랜잭션 -> 로직 -> 커밋 -> 락 해제
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;
    private static final String LOCK_PREFIX = "LOCK:";

//    @Around("@annotation(kr.hhplus.be.server.config.redis.annotation.RedissonLock)")
//    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable{
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        RedissonLock annotation = method.getAnnotation(RedissonLock.class);
//        String lockKey = LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());
//
//        RLock lock = redissonClient.getLock(lockKey);
//
//        try{
//            log.info("스레드 {}: 락 '{}' 시도 중...", Thread.currentThread().getName(), lockKey);
//            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.SECONDS);
//            if(!lockable){
//                log.warn("스레드 {}: 락 '{}' 획득 실패", Thread.currentThread().getName(), lockKey);
//                throw new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED, lockKey);
//            }
//            log.info("스레드 {}: 락 '{}' 획득 성공", Thread.currentThread().getName(), lockKey);
//            Object result = joinPoint.proceed();
//
//            return result;
//        } catch(InterruptedException e){
//            log.error("스레드 {}: 락 획득 중 인터럽트 발생", Thread.currentThread().getName(), e);
//            throw e;
//        } finally{
//            if(lock.isHeldByCurrentThread()) {
//                lock.unlock();
//                log.info("스레드 {}: 락 '{}' 해제", Thread.currentThread().getName(), lockKey);
//            }
//
//        }
//    }

    @Around("@annotation(kr.hhplus.be.server.config.redis.annotation.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable{
        // (1) AOP로 감싸고 있는 메서드의 시그니처와 실제 Method 객체를 가져온다.
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // (2) @RedissonLock 어노테이션을 가져온다.
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);

        // (3) Lock Key를 생성한다. (메서드 이름 + 파라미터 기반)
        //     CustomSpringELParser는 예시로 제시된 코드처럼 파라미터에 EL표현(#productId 등)을 평가해서 문자열로 변환하는 로직.
        String lockKey = method.getName()
                + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());

        // (4) Redisson이 제공하는 분산락 RLock을 가져온다.
        RLock lock = redissonClient.getLock(lockKey);

        // (5) 재시도 횟수와 설정값을 가져오거나, 고정값을 지정한다.
        int maxRetry = 3;   // @RedissonLock에 정의된 retryCount() (예: 기본값 3)
        long waitTime = annotation.waitTime();    // 락을 획득하기 위해 기다리는 최대 시간 (초 단위)
        long leaseTime = annotation.leaseTime();  // 락이 자동 해제되기까지의 시간 (초 단위)
        long retryDelayMillis = 200;              // 재시도 간격(백오프). 필요 시 어노테이션이나 설정 파일에서 주입 가능.

        boolean lockAcquired = false; // 락 획득 성공 여부
        int attempt = 0;             // 현재 시도 횟수

        // (6) 지정된 횟수(maxRetry)만큼 락 획득을 시도한다.
        while (attempt < maxRetry) {
            attempt++;
            try {
                // tryLock: waitTime 동안 락 획득을 시도,
                //          획득 성공 시 true, 실패 시 false 반환
                log.info("스레드 {}: 락 '{}' 획득 시도 (attempt: {}/{})",
                        Thread.currentThread().getName(), lockKey, attempt, maxRetry);

                lockAcquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

                if (lockAcquired) {
                    // 락 획득 성공 시 반복문 종료
                    log.info("스레드 {}: 락 '{}' 획득 성공 (attempt: {}/{})",
                            Thread.currentThread().getName(), lockKey, attempt, maxRetry);
                    break;
                } else {
                    // 락 획득 실패 시
                    log.warn("스레드 {}: 락 '{}' 획득 실패 (attempt: {}/{})",
                            Thread.currentThread().getName(), lockKey, attempt, maxRetry);

                    // 재시도 횟수가 남아있으면 잠시 대기 후 다음 시도
                    if (attempt < maxRetry) {
                        Thread.sleep(retryDelayMillis);
                    }
                }

            } catch (InterruptedException e) {
                // 스레드가 인터럽트되면 현재 스레드의 인터럽트 상태를 복원하고 예외를 재던진다.
                log.error("스레드 {}: 락 '{}' 획득 중 인터럽트 발생",
                        Thread.currentThread().getName(), lockKey, e);
                Thread.currentThread().interrupt();
                throw e;
            }
        }

        // (7) 최종적으로 락을 얻지 못했다면, 비즈니스 로직을 수행하지 않고 종료한다.
        //     상황에 따라 예외를 던지거나, 바로 return 해도 된다.
        if (!lockAcquired) {
            log.error("스레드 {}: 락 '{}' 최종 획득 실패. (재시도 {}회 모두 실패)",
                    Thread.currentThread().getName(), lockKey, maxRetry);
            return null; // 혹은 throw new RuntimeException("락 획득 실패");
        }

        // (8) 락을 획득했다면, 원래의 메서드를 실행(proceed)한 후 finally에서 락을 해제한다.
        try {
            // 비즈니스 로직 실행
            return joinPoint.proceed();
        } finally {
            // (9) 현재 스레드가 락을 보유하고 있다면 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("스레드 {}: 락 '{}' 해제", Thread.currentThread().getName(), lockKey);
            }
        }
    }

//    @Around("@annotation(kr.hhplus.be.server.config.redis.annotation.RedissonLock)")
//    public Object multiLockProcess(ProceedingJoinPoint joinPoint) throws Throwable {
//        // 1) 메서드 시그니처 얻기
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//
//        // 2) 애노테이션 정보 읽기
//        RedissonLock annotation = method.getAnnotation(RedissonLock.class);
//        String spel = annotation.value();
//        long waitTime = annotation.waitTime();
//        long leaseTime = annotation.leaseTime();
//
//        // 3) SpEL로부터 실제 Lock Key(들)을 추출
//        //    예: "#command.orderLine[*].productId" 가 반환하는 값이 List<Long> 형태일 가능성
//        Object keysObj = CustomSpringELParser.getValue(
//                spel,
//                signature.getParameterNames(),
//                joinPoint.getArgs()
//        );
//
//        // 4) keysObj가 단일 String 혹은 List<String>/List<Long> 등인지 체크
//        //    여기서는 List로 가정하고 처리
//        List<String> lockKeyList = new ArrayList<>();
//
//        // 만약 keysObj가 List라면 모두 lockKeyList에 담는다
//        if (keysObj instanceof List<?>) {
//            for (Object keyItem : (List<?>) keysObj) {
//                // String 변환 (필요 시 toString())
//                lockKeyList.add("product:stock:lock:" + keyItem);
//            }
//        }
//        // 혹은 단일 key인 경우
//        else {
//            lockKeyList.add("product:stock:lock:" + keysObj);
//        }
//
//        // 5) RLock 리스트 생성
//        List<RLock> locks = new ArrayList<>();
//        for (String key : lockKeyList) {
//            RLock lock = redissonClient.getLock(key);
//            locks.add(lock);
//        }
//
//        // 6) RMultiLock 생성
//        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
//
//        boolean locked = false;
//        try {
//            log.info("스레드 {}: MultiLock '{}' 획득 시도 (waitTime={}, leaseTime={})",
//                    Thread.currentThread().getName(), lockKeyList, waitTime, leaseTime);
//
//            // 7) tryLock
//            locked = multiLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
//            if (!locked) {
//                log.warn("스레드 {}: MultiLock '{}' 획득 실패", Thread.currentThread().getName(), lockKeyList);
//                // 필요한 경우 예외 throw
//                throw new RuntimeException("MultiLock 획득 실패");
//            }
//            log.info("스레드 {}: MultiLock '{}' 획득 성공", Thread.currentThread().getName(), lockKeyList);
//
//            // 8) 원본 메서드 실행
//            Object result = joinPoint.proceed();
//
//            return result;
//
//        } catch (InterruptedException e) {
//            log.error("스레드 {}: MultiLock 획득 대기 중 인터럽트 발생", Thread.currentThread().getName(), e);
//            Thread.currentThread().interrupt(); // 인터럽트 플래그 설정
//            throw e;
//        } finally {
//            // 9) 락 해제
//            if (locked && multiLock.isHeldByCurrentThread()) {
//                multiLock.unlock();
//                log.info("스레드 {}: MultiLock '{}' 해제", Thread.currentThread().getName(), lockKeyList);
//            }
//        }
//    }
}
