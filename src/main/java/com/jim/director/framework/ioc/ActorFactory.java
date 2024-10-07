package com.jim.director.framework.ioc;

import com.jim.director.framework.annotation.*;
import com.jim.director.framework.tool.PackageScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class ActorFactory {

    /**
     * 所有的Actor信息都会被注册在这里
     */
    private Map<String, ActorDefinition> actorDefinitionRegistry;

    /**
     * 单例Actor实例会存储在这里
     */
    private Map<String, Object> actorRegistry;

    /**
     * 非单例Actor集合实例会存储在这里
     */
    private Map<String, Collection<Object>> prototypeActorRegistry;

    public ActorFactory(){
        actorDefinitionRegistry = new HashMap<>();
        actorRegistry = new HashMap<>();
        prototypeActorRegistry = new HashMap<>();
    }

    public void loadActors(Class<?> starter) throws Exception{
        registerActors(starter);
        instanceActors();
        injectDependencies();
    }

    /**
     * 注册所有Actor
     * @throws Exception
     */
    private void registerActors(Class<?> starter) throws Exception {
        //扫描启动类路径下的所有包
        List<Class<?>> classes = PackageScanner.scanPackage(starter);
        //注册Actor
        for(Class<?> clazz : classes){
            if(clazz.isAnnotationPresent(Actor.class)){
                Actor jimActor = clazz.getAnnotation(Actor.class);
                actorDefinitionRegistry.put(clazz.getName(), createActorDefinition(clazz, jimActor.value()));
            }
            else if(clazz.isAnnotationPresent(Configure.class)){
                actorDefinitionRegistry.put(clazz.getName(), createActorDefinition(clazz, ActorType.SINGLETON));
            }
        }
    }

    /**
     * 实例化所有单例Actor
     * @throws Exception
     */
    private void instanceActors() throws Exception{
        //扫描ActorDefinitionRegistry，实例化Configure
        for(ActorDefinition actorDefinition : actorDefinitionRegistry.values()){
            if(actorDefinition.getActorClass().isAnnotationPresent(Configure.class)){
                actorRegistry.put(actorDefinition.getActorClass().getName(), createActor(actorDefinition));
            }
        }
        //扫描ActorDefinitionRegistry，实例化Actor
        for(ActorDefinition actorDefinition : actorDefinitionRegistry.values()){
            if(actorDefinition.getScope() == ActorType.SINGLETON){
                actorRegistry.put(actorDefinition.getActorClass().getName(), createActor(actorDefinition));
            }
        }
    }


    /**
     * 对所有Actor实例进行依赖注入
     * @throws Exception
     */
    private void injectDependencies() throws Exception{
        //扫描ActorRegistry，对Actor进行依赖注入
        for(Object actor : actorRegistry.values()){
            injectDependency(actor);
        }
    }

    /**
     * 对当前Actor实例进行依赖注入
     * @param actor
     * @throws Exception
     */
    private void injectDependency(Object actor)  throws Exception{
        Field[] fields = actor.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(ActorAutowired.class)){
                Class<?> fieldType = field.getType();
                Object dependency = actorRegistry.get(fieldType.getName());
                if(dependency == null){
                    throw new Exception("Can not inject dependency:" + field.getName());
                }
                field.setAccessible(true);
                field.set(actor, dependency);
            }
            else if(field.isAnnotationPresent(ActorCollectionAutowired.class)){
                //检查字段是否为集合
                if(Collection.class.isAssignableFrom(field.getType())){
                    //获取字段泛型
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                    //检查泛型是否允许多例
                    if(genericClass.isAnnotationPresent(Actor.class)
                            && (genericClass.getAnnotation(Actor.class).value() == ActorType.PROTOTYPE)){
                        //检查是否存在于prototypeActorRegistry
                        Collection<Object> collectionInstance = prototypeActorRegistry.get(genericClass.getName());
                        if(collectionInstance == null){
                            //实例化集合
                            int size = field.getAnnotation(ActorCollectionAutowired.class).value();
                            collectionInstance = (Collection<Object>) field.getType().getDeclaredConstructor(int.class).newInstance(size);
                            //实例化泛型
                            Constructor constructor = genericClass.getDeclaredConstructor();
                            for(int i = 0; i < size; i++){
                                Object genericInstance = constructor.newInstance();
                                //递归注入依赖
                                injectDependency(genericInstance);
                                //添加到集合
                                collectionInstance.add(genericInstance);
                            }
                            //注册到prototypeActorRegistry
                            prototypeActorRegistry.put(genericClass.getName(), collectionInstance);
                        }
                        field.setAccessible(true);
                        field.set(actor, collectionInstance);
                    }
                    else{
                        throw new Exception("Can not inject dependency:" + field.getName());
                    }
                }
                else {
                    throw new Exception("Field is not a type of Collection");
                }
            }
        }
    }

    /**
     * 创建ActorDefinition实例
     * @param clazz
     * @param scope
     * @return
     */
    private ActorDefinition createActorDefinition(Class<?> clazz, ActorType scope){
        return new ActorDefinition(clazz, scope);
    }

    /**
     * 创建Actor实例
     * @param actorDefinition
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Object createActor(ActorDefinition actorDefinition) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return actorDefinition.getActorClass().getDeclaredConstructor().newInstance();
    }

    public Map<String, ActorDefinition> getActorDefinitionRegistry() {
        return actorDefinitionRegistry;
    }

    public void setActorDefinitionRegistry(Map<String, ActorDefinition> actorDefinitionRegistry) {
        this.actorDefinitionRegistry = actorDefinitionRegistry;
    }

    public Map<String, Object> getActorRegistry() {
        return actorRegistry;
    }

    public void setActorRegistry(Map<String, Object> actorRegistry) {
        this.actorRegistry = actorRegistry;
    }

    public Map<String, Collection<Object>> getPrototypeActorRegistry() {
        return prototypeActorRegistry;
    }

    public void setPrototypeActorRegistry(Map<String, Collection<Object>> prototypeActorRegistry) {
        this.prototypeActorRegistry = prototypeActorRegistry;
    }
}
