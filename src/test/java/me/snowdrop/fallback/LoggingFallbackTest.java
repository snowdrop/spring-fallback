package me.snowdrop.fallback;

import com.example.AnnotationOnOneMethod;
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
