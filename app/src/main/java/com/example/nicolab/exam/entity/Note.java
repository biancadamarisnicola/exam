package com.example.nicolab.exam.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bianca on 19.11.2016.
 */

public class Note {
    private String id;
    private String name;
    private long updated;
    private int version;

    public Note(){}

    public Note(String name, long value2, int value3) {
        this.name = name;
        this.updated = value2;
        this.version = value3;
    }

    public Note(String id, String name, long value1, int value2) {
        this.id =id;
        this.name = name;
        this.updated = value1;
        this.version = value2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Note{" +
                "name='" + name + '\'' +
                ", updated = " + updated +
                ", version = " + version +
                ", id = " + id +
                '}';
    }

    public String toStringFancy() {
        return "\nname='" + name + '\'' +
                "\nvalue1 = " + updated +
                "\nvalue2 = " + version;
    }

    public String toJsonString() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("text", name);
            jsonObject.put("updated", updated);
            jsonObject.put("version", version);
            jsonObject.put("id", id);

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
