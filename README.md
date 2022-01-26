[![Maven Central](https://img.shields.io/maven-central/v/me.snowdrop/spring-fallback.svg)](https://mvnrepository.com/artifact/me.snowdrop/spring-fallback)

## Purpose

The purpose of this project is to provide a simple `@Fallback` annotation that will provide
the ability to provide a fallback value for failed calls

## Use cases

A simple use case is to combine this annotation with Istio in order to forgo the need of having to introduce Hystix

## Usage

### Prerequisites

Add the following dependencies to your `pom.xml` 

```xml
<dependency>
    <groupId>me.snowdrop</groupId>
    <artifactId>spring-fallback</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

### Enable fallback

`spring-fallback` is enabled in the same way that caching is enabled in Spring

In one of your configuration classes (or in the main class in case of a Spring Boot application), add `@EnableFallback`.

For example:

```java
package com.example.demo;

import me.snowdrop.fallback.EnableFallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFallback
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```

### Examples

The simplest example where all the defaults are used:

```java
@Component
public class Bean {

    @Fallback
    public String example() {
        throw new RuntimeException();
    }

    public String error() {
        return "error";
    }
}
```

will result in the return of `error` when `Bean.example` is invoked.
This is due to the fact that the default error handler used when `@Fallback` is specified is named `error`

A different method can be specified for example:

```java
@Component
public class Bean {

    @Fallback(fallbackMethod = "fallback")
    public String example() {
        throw new RuntimeException();
    }

    public String fallback() {
        return "error";
    }
} 
```

Fallback methods can also utilize the `ExecutionContext` parameter in order to differentiate error handling
For example:

```java
@Component
public class Bean {

    @Fallback
    public String io() throws IOException{
        throw new IOException();
    }
    
    @Fallback
    public String rt() {
        throw new RuntimeException();
    }    

    public String error(ExecutionContext executionContext) {
        final Throwable t = executionContext.getThrowable();
        if (t instanceof IOException) {
            return "io-fallback";
        }
        else if (t instanceof RuntimeException) {
            return "rt-fallback";
        }
        
        return "default-fallback";
    }
}
```

The `@Fallback` annotation can also be used on a class instead of a method.
In this case each public method of the spring bean will trigger the fallback if it fails.
The aforementioned example could also be rewritten like so:

```java
@Component
@Fallback
public class Bean {

    public String io() throws IOException{
        throw new IOException();
    }
    
    public String rt() {
        throw new RuntimeException();
    }    

    public String error(ExecutionContext executionContext) {
        final Throwable t = executionContext.getThrowable();
        if (t instanceof IOException) {
            return "io-fallback";
        }
        else if (t instanceof RuntimeException) {
            return "rt-fallback";
        }
        
        return "default-fallback";
    }
}
```   

Furthermore, the `@Fallback` annotation can be placed on superclasses or interfaces.

The fallback method can be a static method of some other class. For example the following would work:

```java
public final class FallbackUtil {

    private FallbackUtil() {}

    public static String handle(ExecutionContext executionContext) {
        return "fallback from " + executionContext.getMethod().getName();
    }
}

@Component
public class Bean {

    @Fallback(value = FallbackUtil.class, fallbackMethod = "handle")
    public String example() {
        throw new RuntimeException();
    }
} 
```

Finally, the fallback method can also be a method of some other spring bean as shown in the following example:

```java

@Component
public class FallbackBean {

    public String error(ExecutionContext executionContext) {
        return "fallback from " + executionContext.getMethod().getName();
    }
}

@Component
public class Bean {

    @Fallback(value = FallbackBean.class)
    public String example() {
        throw new RuntimeException();
    }
} 
```

### Multiple fallbacks for a single method / class

The library provides the ability to specify multiple Fallback annotations on a single method or class
as can be seen in the following example:

```java
@Fallback(throwable=IOException.class, value = "ioErrorHandler", order = Integer.MIN_VALUE)
@Fallback(throwable=RuntimeException.class, value ="runtimeErrorHandler", order = Integer.MIN_VALUE)
@Fallback("defaultErrorHandler")
public class Example {
    
    public String someMethod() {
        // could throw various exceptions depending on the codepath
    }
    
    public String ioErrorHandler() {
        return "io";
    }
    
    public String runtimeErrorHandler() {
        return "runtime";
    }
    
    public String defaultErrorHandler() {
        return "default";
    }    
}
```

In the example above if `someMethod` throws an `IOException` (or any of it's subclasses)
then `ioErrorHandler` will handle the error and `"io"` will be returned.
If `someMethod` throws a `RuntimeException` (or any of it's subclasses)
then `runtimeErrorHandler` will handle the error and `"runtime"` will be returned.
Any other exception will be handled by `defaultErrorHandler`.

In cases such as the above it's very important to correctly specify the order (lower values mean that the handler has a higher priority)
