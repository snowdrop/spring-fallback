package me.snowdrop.faulttolerance;

import com.example.fallback.AnnotatedWithProperty;
import com.example.fallback.AnnotatedWithValueBeingOtherClass;
import com.example.fallback.FallbackApplication;
import com.example.fallback.NoMethodAnnotated;
import com.example.fallback.SomeInterface;
import com.example.fallback.SomeMethodsAnnotated;
import com.example.fallback.SomeSubClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FallbackApplication.class)
@TestPropertySource(
        properties = {
                "faulttolerance.name=fallback2",
        }
)
public class FallbackTest {

    @Autowired
    private SomeMethodsAnnotated someMethodsAnnotated;

    @Autowired
    private AnnotatedWithValueBeingOtherClass annotatedWithValueBeingOtherClass;

    @Autowired
    private NoMethodAnnotated noMethodAnnotated;

    @Autowired
    private SomeInterface someInterface;

    @Autowired
    private SomeSubClass someSubClass;

    @Autowired
    private AnnotatedWithProperty annotatedWithProperty;

    @Test
    public void testClassWithNoMethodAnnotated() {
        assertThat(AopUtils.isAopProxy(noMethodAnnotated)).isFalse();
    }

    @Test
    public void testClassWithAnnotatedMethodsIsProxy() {
        assertThat(AopUtils.isAopProxy(someMethodsAnnotated)).isTrue();
    }

    @Test
    public void testClassWhichImplementsInterfaceWithAnnotatedMethodsIsProxy() {
        assertThat(AopUtils.isAopProxy(someInterface)).isTrue();
    }

    @Test
    public void testClassWhichExtentsClassWithAnnotatedMethodsIsProxy() {
        assertThat(AopUtils.isAopProxy(someSubClass)).isTrue();
    }

    @Test
    public void testMethodThatsIsNotAnnotatedAndDoesNotThrowException() {
        assertThat(someMethodsAnnotated.isAnnotatedAndDoesNotThrowException()).isEqualTo("default");
    }

    @Test
    public void testMethodThatsIsAnnotatedButDoesNotThrowException() {
        assertThat(someMethodsAnnotated.isAnnotatedButDoesNotThrowException()).isEqualTo("default");
    }

    @Test
    public void testPrivateDefaultErrorMethod() {
        assertThat(someMethodsAnnotated.defaultErrorSayHi()).isEqualTo("defaultError");
    }

    @Test
    public void testNonDefaultErrorMethodWithParam() {
        assertThat(someMethodsAnnotated.nonDefaultErrorSayHi())
                .contains("faulttolerance")
                .contains("nonDefaultErrorSayHi");
    }

    @Test
    public void testErrorMethodOfSpringBeanDefaultFallbackMethod() {
        assertThat(annotatedWithValueBeingOtherClass.errorHandlerInSpringBeanDefaultMethod()).isEqualTo("default");
    }

    @Test
    public void testErrorMethodOfSpringBeanNonDefaultFallbackMethod() {
        assertThat(annotatedWithValueBeingOtherClass.errorHandlerInSpringBeanNonDefaultMethod())
                .isEqualTo("spring faulttolerance");
    }

    @Test
    public void testErrorMethodOfSpringBeanNonDefaultFallbackMethodWithParam() {
        assertThat(annotatedWithValueBeingOtherClass.errorHandlerInSpringBeanNonDefaultMethodWithParam())
                .contains("spring")
                .contains("faulttolerance")
                .contains("errorHandlerInSpringBeanNonDefaultMethodWithParam");
    }

    @Test
    public void testAnnotatedMethodFromInterface() {
        assertThat(someInterface.annotatedMethod("whatever")).isEqualTo("error");
    }

    @Test(expected = RuntimeException.class)
    public void testNonAnnotatedMethodFromInterface() {
        someInterface.nonAnnotatedMethod("whatever");
    }

    @Test
    public void testAnnotatedMethodFromSuperClass() {
        assertThat(someSubClass.annotatedMethod()).isNull();
    }

    @Test
    public void testNonAnnotatedMethodFromSuperClass() {
        assertThat(someSubClass.nonAnnotatedMethod()).isNotNull();
    }

    @Test
    public void testAnnotatedWithExpression() {
         assertThat(annotatedWithProperty.invoke()).isEqualTo("fallback2");
    }
}
