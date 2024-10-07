package com.jim.director.framework.simulation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class ActionChain extends MethodChain{

    /**
     * 执行顺序
     */
    private int order;

    /**
     * 对象状态
     */
    private Field status;

    /**
     * action状态
     */
    private Object actionStatus;

    public ActionChain(int order, Field status, Object actionStatus, Method method, Object object){
        super(method, object);
        this.order = order;
        this.status = status;
        this.actionStatus = actionStatus;
    }

    /**
     * 执行action
     * @throws Exception
     */
    public void act() throws Exception{
        //检查是否可以执行
        if(status != null && !actionStatus.equals("")){
            status.setAccessible(true);
            Object currStatus = status.get(getObject());
            if(!currStatus.equals(actionStatus)) return;
        }
        getMethod().invoke(getObject());
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Field getStatus() {
        return status;
    }

    public void setStatus(Field status) {
        this.status = status;
    }

    public Object getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(Object actionStatus) {
        this.actionStatus = actionStatus;
    }
}
