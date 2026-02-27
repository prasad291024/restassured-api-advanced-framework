package com.prasad_v.annotations;

import io.qameta.allure.LabelAnnotation;
import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "api")
public @interface API {
    String value();
}
