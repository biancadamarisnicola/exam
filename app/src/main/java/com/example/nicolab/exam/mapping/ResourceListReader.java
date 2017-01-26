package com.example.nicolab.exam.mapping;

import android.util.JsonReader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianca on 01.12.2016.
 */

public class ResourceListReader<E> implements ResourceReader<List<E>, JsonReader> {
    private static final String TAG = ResourceListReader.class.getSimpleName();
    private final ResourceReader<E, JsonReader> resourceReader;

    public ResourceListReader(ResourceReader<E, JsonReader> rr) {
        this.resourceReader = rr;
    }

    @Override
    public List<E> read(JsonReader reader) throws Exception {
        List<E> NoteList = new ArrayList<E>();
        reader.beginArray();
        while (reader.hasNext()) {
            NoteList.add(resourceReader.read(reader));
        }
        reader.endArray();
        Log.d(TAG, NoteList.toString());
        return NoteList;
    }
}
