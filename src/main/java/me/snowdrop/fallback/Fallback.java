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

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Fallback.List.class)
public @interface Fallback {

    /**
     * The default value with result in the class of the method that is being annotated to be used
     */
    Class value() default void.class;

    /**
     * The method to be executed as the fallback method
     * If the name is enclosed in '${}' the it is assumed to be a property
     * and will be looked up in the Spring Environment
     *
     * This method can either have zero parameters, or a single param of type me.snowdrop.fallback.ExecutionContext
     */
    String fallbackMethod() default  "error";

    /**
     * The type of throwable that this Fallback method handles
     *
     * By default all Throwables are handled
     *
     * This is particularly useful when multiple fallbacks are configured for the same method
     * and should be used in conjunction with order
     */
    Class<? extends Throwable> throwable() default Throwable.class;

    /**
     * Order in which to the fallback will be used
     * The smaller the value of order, the more priority the fallback is assigned
     *
     * This is particularly important when multiple fallbacks are configured for the same method
     * and should be used in conjunction with throwable
     */
    int order() default 0;

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Fallback[] value();
    }
}
