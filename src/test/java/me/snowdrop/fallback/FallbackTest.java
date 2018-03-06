package me.snowdrop.fallback;

import com.example.AnnotatedWithValueBeingOtherClass;
import com.example.DefaultFallbackApplication;
import com.example.NoMethodAnnotated;
import com.example.SomeInterface;
import com.example.SomeMethodsAnnotatedWithDefaultValue;
import com.example.SomeSubClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DefaultFallbackApplication.class)
public class FallbackTest {

    @Autowired
    private SomeMethodsAnnotatedWithDefaultValue someMethodsAnnotatedWithDefaultValue;

    @Autowired
    private AnnotatedWithValueBeingOtherClass annotatedWithValueBeingOtherClass;

    @Autowired
    private NoMethodAnnotated noMethodAnnotated;

    @Autowired
    private SomeInterface someInterface;

    @Autowired
    private SomeSubClass someSubClass;

    @Test
    public void testClassWithNoMethodAnnotated() {
        assertThat(AopUtils.isAopProxy(noMethodAnnotated)).isFalse();
    }

    @Test
    public void testClassWithAnnotatedMethodsIsProxy() {
        assertThat(AopUtils.isAopProxy(someMethodsAnnotatedWithDefaultValue)).isTrue();
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
    public void testNonErrorMethod() {
        assertThat(someMethodsAnnotatedWithDefaultValue.normalSayHi()).isEqualTo("hi");
    }

    @Test
    public void testDefaultErrorMethod() {
        assertThat(someMethodsAnnotatedWithDefaultValue.defaultErrorSayHi()).isEqualTo("defaultError");
    }

    @Test
    public void testNonDefaultErrorMethodWithParam() {
        assertThat(someMethodsAnnotatedWithDefaultValue.nonDefaultErrorSayHi())
                .contains("fallback")
                .contains("nonDefaultErrorSayHi");
    }

    @Test
    public void testDefaultErrorMethodOfOtherClass() {
        assertThat(annotatedWithValueBeingOtherClass.method1()).isEqualTo("error");
    }

    @Test
    public void testDefaultErrorMethodOfOtherClassWithParam() {
        assertThat(annotatedWithValueBeingOtherClass.method2())
                .contains("fallback")
                .contains("method2");
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
}
