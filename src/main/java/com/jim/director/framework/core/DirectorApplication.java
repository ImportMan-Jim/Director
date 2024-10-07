package com.jim.director.framework.core;

import com.jim.director.framework.ioc.ActorFactory;
import com.jim.director.framework.log.LogSystem;
import com.jim.director.framework.simulation.SimulationContext;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class DirectorApplication {

    public static void run(Class<?> starter){
        //加载actor
        ActorFactory actorFactory = new ActorFactory();
        try{
            actorFactory.loadActors(starter);
        }catch (Exception e){
            e.printStackTrace();
        }
        //加载Log
        LogSystem logSystem = new LogSystem();
        logSystem.loadLog(actorFactory);
        //加载仿真环境
        SimulationContext simulationContext = new SimulationContext();
        simulationContext.loadSimulation(actorFactory, logSystem);
        //开始仿真
        simulationContext.start();
    }
}
