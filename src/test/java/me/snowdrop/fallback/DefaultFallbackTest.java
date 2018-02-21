package me.snowdrop.fallback;

import com.example2.DefaultFallbackApplication;
import com.example2.Dummy;
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
    private Dummy dummy;

    @Test
    public void testNonErrorMethod() {
        assertThat(dummy.normalSayHi()).isEqualTo("hi");
    }

    @Test
    public void testDefaultErrorMethod() {
        assertThat(dummy.defaultErrorSayHi()).isEqualTo("defaultError");
    }

    @Test
    public void testNonDefaultErrorMethod() {
        assertThat(dummy.nonDefaultErrorSayHi()).isEqualTo("nonDefaultError");
    }
}
