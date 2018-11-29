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
import com.example.MultipleFallbacksForClass;
import com.example.SomeSuperClassWithMultipleFallbacksOnClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DefaultFallbackApplication.class)
public class MultipleFallbacksForClassTest {

    @Autowired
    private MultipleFallbacksForClass multipleFallbacksForClass;

    @Autowired
    private SomeSuperClassWithMultipleFallbacksOnClass someSuperClassWithMultipleFallbacksOnClass;

    @Test
    public void testClassWhichImplementsInterfaceWithAnnotatedMethodsIsProxy() {
        assertThat(AopUtils.isAopProxy(multipleFallbacksForClass)).isTrue();
    }

    @Test
    public void testNoExceptionShouldBeThrown() throws Exception {
        assertThat(multipleFallbacksForClass.dummyThrower(null)).isEqualTo("noException");
    }

    @Test
    public void testProperGenericExceptionHandling() throws Exception {
        assertThat(multipleFallbacksForClass.dummyThrower(new Exception("dummy"))).isEqualTo("generic");
    }

    @Test
    public void testClassThatExtendsClassWithMethodsAnnotatedWithMultipleFallacksIsAProxy() {
        assertThat(AopUtils.isAopProxy(someSuperClassWithMultipleFallbacksOnClass)).isTrue();
    }

    @Test
    public void testFirstMatchingExceptionOfSuperClass() throws Exception {
        assertThat(someSuperClassWithMultipleFallbacksOnClass.dummy(new FileNotFoundException()))
                .isEqualTo("io");
    }

    @Test
    public void testSecondMatchingExceptionOfSuperClass() throws Exception {
        assertThat(someSuperClassWithMultipleFallbacksOnClass.dummy(new NullPointerException()))
                .isEqualTo("runtime");
    }

    @Test(expected = Exception.class)
    public void testUnhandledException() throws Exception {
        someSuperClassWithMultipleFallbacksOnClass.dummy(new Exception());
    }
}
