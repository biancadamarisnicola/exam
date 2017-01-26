package com.example.nicolab.exam.mapping;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by bianca on 30.11.2016.
 */
public interface ResourceReader<E, Reader> {
    E read(Reader reader) throws IOException, JSONException, Exception;
}
