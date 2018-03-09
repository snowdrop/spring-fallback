package me.snowdrop.faulttolerance;

import com.example.mixed.ClassWithBothAnnotations;
import com.example.mixed.MixedApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MixedApplication.class)
public class MixedTest {

    @Autowired
    private ClassWithBothAnnotations classWithBothAnnotations;

    @Before
    public void setUp() {
        classWithBothAnnotations.reset();
    }

    @Test
    public void testClassIsProxy() {
        assertThat(AopUtils.isAopProxy(classWithBothAnnotations)).isTrue();
    }

    @Test
    public void testAnnotatedMethodThatRecovers() {
        final String response = classWithBothAnnotations.annotatedMethodThatRecovers();

        assertThat(classWithBothAnnotations.getNumberOfInvocations()).isEqualTo(3);
        assertThat(response).isEqualToIgnoringCase("default");
    }

    @Test
    public void testAnnotatedMethodThatDoesNotRecover() {
        final String response = classWithBothAnnotations.annotatedMethodThatDoesNotRecover();

        assertThat(classWithBothAnnotations.getNumberOfInvocations()).isEqualTo(4);
        assertThat(response).isEqualToIgnoringCase("fallback");
    }
}
