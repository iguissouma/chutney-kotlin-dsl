package com.chutneytesting.kotlin;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable // for IDE support
public @interface ChutneyTest {
    String value() default "";
    String environment() default "";
}
