package me.snowdrop.fallback;

import com.example.AnnotationOnOneMethod;
import com.example.NoMethodAnnotated;
import me.snowdrop.fallback.support.AbstractSpringTest;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.rule.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;

public class FallbackOutputCaptureTest extends AbstractSpringTest {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    AnnotationOnOneMethod annotationOnOneMethod;

    @Autowired
    NoMethodAnnotated noMethodAnnotated;

    @Test
    public void testAnnotatedMethod() {
        annotationOnOneMethod.annotatedMethod();

        assertThat(outputCapture.toString())
                .containsOnlyOnce("FallbackAdvice - Before")
                .containsOnlyOnce("FallbackAdvice - After");
    }

    @Test
    public void testMethodNotAnnotatedInClassWithOtherMethodThatIsAnnotated() {
        annotationOnOneMethod.nonAnnotatedMethod();

        assertThat(outputCapture.toString()).doesNotContain("FallbackAdvice");
    }

    @Test
    public void testMethodOfClassThatHasNoAnnotatedMethods() {
        noMethodAnnotated.dummy();

        assertThat(outputCapture.toString()).doesNotContain("FallbackAdvice");
    }
}
