package com.example.nicolab.exam.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bianca on 19.11.2016.
 */

public class Entity {
    private String id;
    private String name;
    private String value1;
    private String value2;
    private String value3;
    private String value4;

    public Entity(){}

    public Entity(String name, String value1, String value2, String value3, String value4) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    public Entity(String id, String name, String value1, String value2, String value3, String value4) {
        this.id =id;
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                ", value1 = " + value1 +
                ", value2 = " + value2 +
                ", value3 = " + value3 +
                ", value4 = " + value4 +
                ", id = " + id +
                '}';
    }

    public String toStringFancy() {
        return "\nname='" + name + '\'' +
                "\nvalue1 = " + value1 +
                "\nvalue2 = " + value2 +
                "\nvalue3 = " + value3 +
                "\nvalue4 = " + value4;
    }

    public String toJsonString() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("value1", value1);
            jsonObject.put("value2", value2);
            jsonObject.put("value3", value3);
            jsonObject.put("value4", value4);

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}
