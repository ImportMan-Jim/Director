package com.jim.director.framework.simulation;

import java.lang.reflect.Method;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class MethodChain {

    /**
     * 执行的方法
     */
    private Method method;

    /**
     * 被执行的对象
     */
    private Object object;

    public MethodChain(Method method, Object object) {
        this.method = method;
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
