package com.jim.director.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: Jim
 * date: 2024/10/2
 * info: 与@JimActor类似，不过在实例化时，@JimConfigure标记的类会优先实例化
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configure {
}
