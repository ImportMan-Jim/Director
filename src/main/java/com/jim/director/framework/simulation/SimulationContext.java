package com.jim.director.framework.simulation;

import com.jim.director.framework.annotation.Action;
import com.jim.director.framework.annotation.Init;
import com.jim.director.framework.annotation.Order;
import com.jim.director.framework.annotation.Status;
import com.jim.director.framework.ioc.ActorFactory;
import com.jim.director.framework.log.LogSystem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class SimulationContext {

    /**
     * init执行链
     */
    private List<MethodChain> initChains;

    /**
     * action执行链，每一帧按照该链执行
     */
    private List<ActionChain> actionChains;

    /**
     * 日志系统
     */
    private LogSystem logSystem;

    public SimulationContext(){
        initChains = new ArrayList<>();
        actionChains = new ArrayList<>();
    }

    /**
     * 加载执行链
     * @param actorFactory
     * @param logSystem
     */
    public void loadSimulation(ActorFactory actorFactory, LogSystem logSystem){
        this.logSystem = logSystem;
        Map<String, Object> actorRegistry = actorFactory.getActorRegistry();
        Map<String, Collection<Object>> prototypeActorRegistry = actorFactory.getPrototypeActorRegistry();
        //扫描所有actor
        for(Object actor : actorRegistry.values()){
            addInitChain(actor);
            addActionChain(actor);
        }
        //扫描所有prototypeActor
        for(Collection<Object> prototypeActors : prototypeActorRegistry.values()){
            for(Object prototypeActor : prototypeActors){
                addInitChain(prototypeActor);
                addActionChain(prototypeActor);
            }
        }
        //根据order排序
        Collections.sort(actionChains, Comparator.comparingInt(ActionChain::getOrder));
    }

    /**
     * 开始仿真
     */
    public void start(){
        init();
        run();
    }

    /**
     * 执行init
     */
    private void init(){
        for(MethodChain methodChain : initChains){
            Method method = methodChain.getMethod();
            try{
                method.invoke(methodChain.getObject());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 运行仿真
     */
    private void run(){
        int simulateDuration = BasicConfigure.getSimulateDuration();
        for(int frame = 0; frame < simulateDuration; frame++){
            //执行action
            for(ActionChain actionChain : actionChains){
                try{
                    actionChain.act();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            //执行Log
            logSystem.log(frame);
        }
    }

    /**
     * 将当前actor中的action加入action链
     * @param actor
     */
    private void addActionChain(Object actor){
        Class<?> clazz = actor.getClass();
        if(clazz.isAnnotationPresent(Order.class)){
            int order = clazz.getAnnotation(Order.class).value();
            Field[] fields = clazz.getDeclaredFields();
            Field status = null;
            for(Field field : fields){
                if(field.isAnnotationPresent(Status.class)){
                    status = field;
                    break;
                }
            }
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(Action.class)){
                    String actionStatus = method.getAnnotation(Action.class).status();
                    actionChains.add(new ActionChain(order, status, actionStatus, method, actor));
                }
            }
        }
    }

    /**
     * 将当前actor中的init加入init链
     * @param actor
     */
    private void addInitChain(Object actor){
        Class<?> clazz = actor.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(Init.class)){
                initChains.add(new MethodChain(method, actor));
            }
        }
    }

    public List<ActionChain> getActionChains() {
        return actionChains;
    }

    public void setActionChains(List<ActionChain> actionChains) {
        this.actionChains = actionChains;
    }


}
