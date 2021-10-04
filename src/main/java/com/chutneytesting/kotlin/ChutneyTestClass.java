package com.chutneytesting.kotlin;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable // for IDE support
public @interface ChutneyTestClass {
}
