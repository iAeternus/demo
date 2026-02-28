package com.ricky;

import com.ricky.domain.Student;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://zhuanlan.zhihu.com/p/1917584196890785099">一文吃透 Spring 表达式语言（SpEL），Spring 高级开发者必须掌握！</a>
 */
@SpringBootTest
public class SpelTest {

    @Resource
    private ApplicationContext applicationContext;

    // 字面量表达式
    @Test
    void literal_expression() {
        SpelExpressionParser parser = new SpelExpressionParser();

        // 字符串
        Expression expr = parser.parseExpression("'hello SpEL!'");
        String str = expr.getValue(String.class);
        System.out.println(str);

        // 数字
        expr = parser.parseExpression("6.02");
        Double num = expr.getValue(Double.class);
        System.out.println(num);

        // boolean
        expr = parser.parseExpression("true");
        Boolean trueValue = expr.getValue(Boolean.class);
        System.out.println(trueValue);

        // null
        expr = parser.parseExpression("null");
        Object nullValue = expr.getValue();
        System.out.println(nullValue);
    }

    // 属性访问与导航
    @Test
    void attribute_access_and_navigation() {
        Student student = Student.test();

        EvaluationContext context = new StandardEvaluationContext(student);
        ExpressionParser parser = new SpelExpressionParser();

        Expression expr = parser.parseExpression("name");
        String name = (String) expr.getValue(context);
        System.out.println(name); // zhangSan

        // 属性访问
        expr = parser.parseExpression("birthPlace.country + ',' + birthPlace.city");
        String birthPlaceStr = (String) expr.getValue(context);
        System.out.println(birthPlaceStr);

        // 访问列表
        expr = parser.parseExpression("hobbies[0]");
        String hobby = (String) expr.getValue(context);
        System.out.println(hobby); // 原神

        // 访问映射
        expr = parser.parseExpression("scores['语文']");
        Integer score = (Integer) expr.getValue(context);
        System.out.println(score); // 85
    }

    // 内联列表与映射
    @Test
    void inline_list_and_map() {
        ExpressionParser parser = new SpelExpressionParser();

        Expression expr = parser.parseExpression("{1, 2, 3}");
        List<?> list = expr.getValue(List.class);
        System.out.println(list);

        expr = parser.parseExpression("{name: 'Tesla', nationality: 'Serbian'}");
        Map<?, ?> map = expr.getValue(Map.class);
        System.out.println(map);
    }

    // 类型表达式
    // 使用特殊的 T 运算符来指定 java.lang.Class（类型）的实例，也通过使用此运算符来调用静态方法
    @Test
    void type_expression() {
        ExpressionParser parser = new SpelExpressionParser();

        // 指定特定类型
        Class<?> dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println(dateClass);

        // 调用静态方法
        Double randomNum = parser.parseExpression("T(java.lang.Math).random()").getValue(Double.class);
        System.out.println(randomNum);
    }

    // 方法调用
    // 可以使用典型的Java编程语法来调用方法，也可以直接对字符串或数字等字面量调用方法
    @Test
    void method_call() {
        ExpressionParser parser = new SpelExpressionParser();

        Expression expr = parser.parseExpression("'hello, SpEL!'.toUpperCase()");
        String str = expr.getValue(String.class);
        System.out.println(str); // HELLO, SPEL!
    }

    // Bean引用
    // 如果求值上下文已配置了Bean解析器，可以通过使用前缀@从表达式中查找Bean，访问Bean属性或方法
    @Test
    void bean_reference() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));

        Expression expr = parser.parseExpression("@student.getName()");
        String name = (String) expr.getValue(context);
        System.out.println(name);
    }

    // 变量
    // 变量是通过在 EvaluationContext 实现中使用 setVariable() 方法来设置的，使用 #variableName语法在表达式中引用变量
    @Test
    void variables() {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("name", "zhangSan");

        Expression expr = parser.parseExpression("#name");
        String name = (String) expr.getValue(context);
        System.out.println(name); // zhangSan
    }

    // 安全导航运算符
    // 安全导航运算符（?.）避免 NullPointerException，在对象可能为 null 时特别有用
    @Test
    void safe_navigation_operator() {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("name", null);

        Expression expr = parser.parseExpression("#name?.toUpperCase()");
        String result = expr.getValue(context, String.class);
        System.out.println(result);
    }


}
