package com.example.nicolab.exam.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bianca on 30.11.2016.
 */
public class Issue {
    private Map<String, String> details = new HashMap<>();

    public Issue add(String name, String value) {
        details.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : details.keySet()) {
            sb.append(key).append(": ").append(details.get(key));
        }
        return sb.toString();
    }
}
