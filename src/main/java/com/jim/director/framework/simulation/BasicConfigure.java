package com.jim.director.framework.simulation;

/**
 * author: Jim
 * date: 2024/10/3
 * info:
 */

public class BasicConfigure {

    /**
     * 仿真时长
     */
    private static int simulateDuration = 1000;

    public static int getSimulateDuration(){
        return simulateDuration;
    }

    public void setSimulateDuration(int simulateDuration){
        this.simulateDuration = simulateDuration;
    }
}
