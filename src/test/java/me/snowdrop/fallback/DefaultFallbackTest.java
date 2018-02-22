package me.snowdrop.fallback;

import com.example2.AnnotatedWithValueBeingOtherClass;
import com.example2.DefaultFallbackApplication;
import com.example2.SomeMethodsAnnotatedWithDefaultValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DefaultFallbackApplication.class)
public class DefaultFallbackTest {

    @Autowired
    private SomeMethodsAnnotatedWithDefaultValue someMethodsAnnotatedWithDefaultValue;

    @Autowired
    private AnnotatedWithValueBeingOtherClass annotatedWithValueBeingOtherClass;

    @Test
    public void testNonErrorMethod() {
        assertThat(someMethodsAnnotatedWithDefaultValue.normalSayHi()).isEqualTo("hi");
    }

    @Test
    public void testDefaultErrorMethod() {
        assertThat(someMethodsAnnotatedWithDefaultValue.defaultErrorSayHi()).isEqualTo("defaultError");
    }

    @Test
    public void testNonDefaultErrorMethod() {
        assertThat(someMethodsAnnotatedWithDefaultValue.nonDefaultErrorSayHi()).isEqualTo("nonDefaultError");
    }

    @Test
    public void testDefaultErrorMethodOfOtherClass() {
        assertThat(annotatedWithValueBeingOtherClass.perform()).isEqualTo("error");
    }
}
