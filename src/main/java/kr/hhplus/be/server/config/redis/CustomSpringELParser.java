package kr.hhplus.be.server.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 커스텀 스프링 표현식 Parser
 * 전달받은 Lock이름을 Spring Expression Language로 파싱하여 읽어오도록 함
 */
@Slf4j
public class CustomSpringELParser {
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }

//    private static final SpelExpressionParser parser = new SpelExpressionParser();
//
//    /**
//     * SpEL 식과 파라미터 정보를 통해
//     * 해당 식이 가리키는 Object를 반환
//     */
//    public static Object getValue(String spel, String[] paramNames, Object[] args) {
//        // 1) 스프링 EL 평가 컨텍스트 생성
//        EvaluationContext context = new StandardEvaluationContext();
//
//        // 2) 메서드 파라미터 이름/값들을 컨텍스트에 넣음
//        for (int i = 0; i < paramNames.length; i++) {
//            context.setVariable(paramNames[i], args[i]);
//        }
//
//        // 3) 식 파싱
//        Expression expression = parser.parseExpression(spel);
//
//        // 4) 식 평가 결과 반환
//        Object value = expression.getValue(context);
//        log.debug("SpEL '{}' -> {}", spel, value);
//
//        return value;
//    }
}
