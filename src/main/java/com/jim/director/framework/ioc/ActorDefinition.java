package com.jim.director.framework.ioc;

import com.jim.director.framework.annotation.ActorType;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */
public class ActorDefinition {
    private Class<?> actorClass;
    private ActorType scope = ActorType.SINGLETON;

    public ActorDefinition(Class<?> actorClass){
        this.actorClass = actorClass;
    }

    public ActorDefinition(Class<?> actorClass, ActorType scope){
        this.actorClass = actorClass;
        this.scope = scope;
    }

    public Class<?> getActorClass() {
        return actorClass;
    }

    public void setActorClass(Class<?> actorClass) {
        this.actorClass = actorClass;
    }

    public ActorType getScope() {
        return scope;
    }

    public void setScope(ActorType scope) {
        this.scope = scope;
    }
}
