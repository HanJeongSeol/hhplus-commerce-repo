package kr.hhplus.be.server.support.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.springframework.web.servlet.DispatcherServlet.EXCEPTION_ATTRIBUTE;

@Slf4j
public class ExceptionLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // BusinessException이 발생하면 GlobalExceptionHandler이 먼저 처리하기때문에 afterCompletion으로는 ex 값이 null로 들어온다.
        Exception exception = (Exception) request.getAttribute(EXCEPTION_ATTRIBUTE);

        if(exception instanceof BusinessException businessException){
            // BusinessException인 경우, 로깅 처리
            logBusinessException(businessException);
        }
        MDC.clear();
    }

    private void logBusinessException(BusinessException ex){
        ErrorCode errorCode = ex.getErrorCode();
        String detailMessage = ex.getDetailMessage();

        // 파라미터화된 로깅 사용
        switch (errorCode.getHttpStatus().value()) {
            case 400, 403, 404 -> log.warn("[{}] {} - {}", errorCode.name(), errorCode.getMessage(), detailMessage);
            case 500 -> log.error("[{}] {} - {}", errorCode.name(), errorCode.getMessage(), detailMessage);
            default -> log.error("Unhandled error: [{}] {} - {}", errorCode.name(), errorCode.getMessage(), detailMessage);
        }
    }
}
