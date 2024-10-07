package com.jim.director.framework.log;

import com.jim.director.framework.annotation.Log;
import com.jim.director.framework.annotation.LogIgnore;
import com.jim.director.framework.ioc.ActorFactory;
import com.jim.director.framework.tool.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * author: Jim
 * date: 2024/10/3
 * info:
 */

public class LogSystem {

    private Map<String, List<LogItem>> logMapping;

    public LogSystem(){
        logMapping = new HashMap<>();
    }

    /**
     * 加载Log
     * @param actorFactory
     */
    public void loadLog(ActorFactory actorFactory){
        Map<String, Object> actorRegistry = actorFactory.getActorRegistry();
        Map<String, Collection<Object>> prototypeActorRegistry = actorFactory.getPrototypeActorRegistry();
        //扫描所有actor
        for(Object actor : actorRegistry.values()){
            logActor(actor);
        }
        //扫描所有prototypeActor
        for(Collection<Object> prototypeActors : prototypeActorRegistry.values()){
            for(Object prototypeActor : prototypeActors){
                logActor(prototypeActor);
            }
        }
        //打印日志banner
        for(String target : logMapping.keySet()){
            PrintWriter out = null;
            try {
                if(target.equals("System.out")){
                    out = new PrintWriter(System.out);
                }
                else {
                    File file = new File(target);
                    out = new PrintWriter(new FileOutputStream(file, true));
                }
                out.print("\n>>>>>>>>>>>>>>>>>>>> New simulation starts at " + LocalDateTime.now() + " <<<<<<<<<<<<<<<<<<<<\n");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(out != null){
                    out.flush();
                    // 仅在文件输出时关闭流
                    if (!target.equals("System.out")) {
                        out.close();
                    }
                }
            }
        }
    }

    /**
     * 记录日志
     * @param frame
     */
    public void log(int frame){
        for(String target : logMapping.keySet()){
            PrintWriter out = null;
            try {
                if(target.equals("System.out")){
                    out = new PrintWriter(System.out);
                }
                else {
                    File file = new File(target);
                    out = new PrintWriter(new FileOutputStream(file, true));
                }
                List<LogItem> logItems = logMapping.get(target);
                for(LogItem logItem : logItems){
                    //检查是否需要记录日志
                    if(frame % logItem.getPeriod() == 0){
                        out.print("frame: ");
                        out.print(frame);
                        out.print("; ");
                        out.print(logItem.toString());
                        out.print("\n");
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(out != null){
                    out.flush();
                    // 仅在文件输出时关闭流
                    if (!target.equals("System.out")) {
                        out.close();
                    }
                }
            }
        }
    }



    /**
     * 记录当前actor的log
     * @param actor
     */
    private void logActor(Object actor){
        Class<?> clazz = actor.getClass();
        if(clazz.isAnnotationPresent(Log.class)){
            Log jimLog = clazz.getAnnotation(Log.class);
            int period = jimLog.period();
            String target = jimLog.target();
            if(!target.equals("System.out")){
                target = FileUtils.getAbsolutePathFromClassAndPath(clazz, target);
            }
            List<Field> fields = getAllFields(clazz);
            Map<String, Field> fieldMapping = new HashMap<>();
            for(Field field : fields){
                if(field.isAnnotationPresent(LogIgnore.class)) continue;
                fieldMapping.put(field.getName(), field);
            }
            LogItem logItem = new LogItem(period, actor, fieldMapping);
            List<LogItem> logItems = logMapping.computeIfAbsent(target, key -> new ArrayList<>());
            logItems.add(logItem);
        }
    }

    /**
     * 获取类的所有Field，包括继承的
     * @param clazz
     * @return
     */
    private List<Field> getAllFields(Class<?> clazz){
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class && clazz != null){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

}
