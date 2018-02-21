package me.snowdrop.fallback;

import com.example.AnnotationOnSomeMethods;
import com.example.LoggingFallbackApplication;
import com.example.NoMethodAnnotated;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoggingFallbackApplication.class)
public class LoggingFallbackTest {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    AnnotationOnSomeMethods annotationOnSomeMethods;

    @Autowired
    NoMethodAnnotated noMethodAnnotated;

    @Test
    public void testAnnotatedMethod() {
        annotationOnSomeMethods.annotatedMethod();

        assertLoggingFallbackCalled();
    }

    @Test
    public void testAnnotatedVoidMethod() {
        annotationOnSomeMethods.annotatedVoidMethod();

        assertLoggingFallbackCalled();
    }

    @Test
    public void testMethodNotAnnotatedMethodInClassWithOtherMethodThatIsAnnotated() {
        annotationOnSomeMethods.nonAnnotatedMethod();

        assertLoggingFallbackNotCalled();
    }

    @Test
    public void testMethodNotAnnotatedVoidMethodInClassWithOtherMethodThatIsAnnotated() {
        annotationOnSomeMethods.nonAnnotatedMethod();

        assertLoggingFallbackNotCalled();
    }

    @Test
    public void testMethodOfClassThatHasNoAnnotatedMethods() {
        noMethodAnnotated.dummy();

        assertLoggingFallbackNotCalled();
    }

    private void assertLoggingFallbackCalled() {
        assertThat(outputCapture.toString())
                .containsOnlyOnce("FallbackAdvice - Before")
                .containsOnlyOnce("FallbackAdvice - After");
    }

    private void assertLoggingFallbackNotCalled() {
        assertThat(outputCapture.toString()).doesNotContain("FallbackAdvice");
    }
}
