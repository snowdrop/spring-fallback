/*
 * Copyright (C) 2018 Red Hat inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.fallback;

import com.example.DefaultFallbackApplication;
import com.example.MultipleFallbacksForMethod;
import com.example.SomeSubClassOfSuperWithMultipleFallbacksOnMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DefaultFallbackApplication.class)
public class MultipleFallbacksForMethodTest {

    @Autowired
    private MultipleFallbacksForMethod multipleFallbacksForMethod;

    @Autowired
    private SomeSubClassOfSuperWithMultipleFallbacksOnMethod someSubClassOfSuperWithMultipleFallbacks;

    @Test
    public void testClassWithMethodsAnnotatedWithMultipleFallacksIsAProxy() {
        assertThat(AopUtils.isAopProxy(multipleFallbacksForMethod)).isTrue();
    }

    @Test
    public void testNoExceptionShouldBeThrown() throws Exception {
        assertThat(multipleFallbacksForMethod.dummyThrower(null)).isEqualTo("noException");
    }

    @Test
    public void testMatchingLowestOrderExceptionHandling() throws Exception {
        assertThat(multipleFallbacksForMethod.dummyThrower(new Exception("dummy"))).isEqualTo("generic");
    }

    @Test
    public void testSubclassLowestOrderExceptionHandling() throws Exception {
        assertThat(multipleFallbacksForMethod.dummyThrower(new TimeoutException())).isEqualTo("generic");
    }

    @Test
    public void testMatchingHighestOrderExceptionHandling() throws Exception {
        assertThat(multipleFallbacksForMethod.dummyThrower(new FileNotFoundException())).isEqualTo("file");
    }

    @Test
    public void testMatchingMidOrderExceptionHandling() throws Exception {
        assertThat(multipleFallbacksForMethod.dummyThrower(new IOException())).isEqualTo("io");
    }

    @Test
    public void testClassThatExtendsClassWithMethodsAnnotatedWithMultipleFallacksIsAProxy() {
        assertThat(AopUtils.isAopProxy(someSubClassOfSuperWithMultipleFallbacks)).isTrue();
    }

    @Test
    public void testNonAnnotatedMethodFromSuperClass() {
        assertThat(someSubClassOfSuperWithMultipleFallbacks.nonAnnotatedMethod()).isNotNull();
    }

    @Test
    public void testFirstMatchingExceptionOfSuperClass() throws Exception {
        assertThat(someSubClassOfSuperWithMultipleFallbacks.annotatedMethod(new Exception()))
                .isEqualTo("generic");
    }

    @Test
    public void testSecondMatchingExceptionOfSuperClass() throws Exception {
        assertThat(someSubClassOfSuperWithMultipleFallbacks.annotatedMethod(new RuntimeException()))
                .isEqualTo("runtime");
    }

}
