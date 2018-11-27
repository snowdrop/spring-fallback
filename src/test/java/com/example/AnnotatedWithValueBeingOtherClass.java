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
package com.example;

import me.snowdrop.fallback.Fallback;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedWithValueBeingOtherClass {

    @Fallback(StaticErrorHandlerWithoutParam.class)
    public String errorHandlerInStaticClass() {
        throw new RuntimeException();
    }

    @Fallback(StaticErrorHandlerWithParam.class)
    public String errorHandlerInStaticClassWithParam() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "springFallback")
    public String errorHandlerInSpringBean() {
        throw new RuntimeException();
    }

    @Fallback(value = SpringErrorHandler.class, fallbackMethod = "springFallbackWithParam")
    public String errorHandlerInSpringBeanWithParam() {
        throw new RuntimeException();
    }
}
