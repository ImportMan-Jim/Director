package com.jim.director.framework.log;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * author: Jim
 * date: 2024/10/3
 * info:
 */

public class LogItem {

    private int period;

    private Object actor;

    private Map<String, Field> fields;

    public LogItem(int period, Object actor, Map<String, Field> fields) {
        this.period = period;
        this.actor = actor;
        this.fields = fields;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Object getActor() {
        return actor;
    }

    public void setActor(Object actor) {
        this.actor = actor;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("actor: [");
        sb.append(actor.getClass().getSimpleName());
        sb.append("]");
        sb.append(actor.hashCode());
        sb.append("; fields: {");
        fields.forEach((k, v) ->{
            sb.append(k);
            sb.append(": ");
            v.setAccessible(true);
            try {
                sb.append(v.get(actor));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(", ");
        });
        sb.append("}; ");
        return sb.toString();
    }
}
