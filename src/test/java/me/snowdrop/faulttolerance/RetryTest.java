package me.snowdrop.faulttolerance;

import com.example.retry.MethodsAnnotated;
import com.example.retry.NoMethodAnnotated;
import com.example.retry.RetryApplication;
import com.example.retry.SomeInterface;
import com.example.retry.SomeSubClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RetryApplication.class)
public class RetryTest {

    @Autowired
    private NoMethodAnnotated noMethodAnnotated;

    @Autowired
    private MethodsAnnotated methodsAnnotated;

    @Autowired
    private SomeInterface someInterface;

    @Autowired
    private SomeSubClass someSubClass;

    @Before
    public void setUp() {
        methodsAnnotated.reset();
        someInterface.reset();
        someSubClass.reset();
    }

    @Test
    public void testClassWithNoMethodAnnotated() {
        assertThat(AopUtils.isAopProxy(noMethodAnnotated)).isFalse();
    }

    @Test
    public void testClassWithMethodAnnotated() {
        assertThat(AopUtils.isAopProxy(methodsAnnotated)).isTrue();
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
    public void testMethodThatThrowsRetryableExceptionAndRecovers() {
        methodsAnnotated.throwsRetryableExceptionAndRecovers();

        assertThat(methodsAnnotated.getNumberOfInvocations()).isEqualTo(3);
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodThatThrowsRetryableExceptionAndDoesNotRecover() {
        methodsAnnotated.throwsRetryableExceptionAndDoesNotRecover();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodThatThrowsExceptionAndIsNotAnnotatedWithRetry() {
        methodsAnnotated.throwsExceptionAndIsNotAnnotatedWithRetry();
    }

    @Test
    public void testMethodThatThrowsNonRetryableException() {
        try {
            methodsAnnotated.throwsNonRetryableException();
            fail("The method should have thrown an exception");
        } catch (RuntimeException ignored) {
            assertThat(methodsAnnotated.getNumberOfInvocations()).isEqualTo(1);
        }
    }

    @Test
    public void testInterfaceMethodThatIsAnnotated() {
        final String response = someInterface.annotatedMethod("in");

        assertThat(response).isEqualToIgnoringCase("out");
    }

    @Test(expected = RuntimeException.class)
    public void testInterfaceMethodThatIsNotAnnotated() {
        someInterface.nonAnnotatedMethod("in");
    }

    @Test
    public void testSuperclassMethodThatIsAnnotated() {
        assertThat(someSubClass.annotatedMethod()).isNotNull();
    }

    @Test(expected = RuntimeException.class)
    public void testSuperclassMethodThatIsNotAnnotated() {
        someSubClass.nonAnnotatedMethod();
    }
}
