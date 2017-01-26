package com.example.nicolab.exam.mapping;

import android.util.JsonReader;
import android.util.Log;

import com.example.nicolab.exam.entity.Entity;

import java.io.IOException;

import static com.example.nicolab.exam.mapping.Api.Entity.ID;
import static com.example.nicolab.exam.mapping.Api.Entity.NAME;
import static com.example.nicolab.exam.mapping.Api.Entity.VALUE1;
import static com.example.nicolab.exam.mapping.Api.Entity.VALUE2;
import static com.example.nicolab.exam.mapping.Api.Entity.VALUE3;
import static com.example.nicolab.exam.mapping.Api.Entity.VALUE4;

/**
 * Created by bianca on 01.12.2016.
 */
public class EntityReader implements ResourceReader<Entity, JsonReader>{
    private static final String TAG = EntityReader.class.getSimpleName();

    @Override
    public Entity read(JsonReader reader) throws IOException {
        Entity aliment = new Entity();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(NAME)) {
                aliment.setName(reader.nextString());
            } else if (name.equals(VALUE1)) {
                aliment.setValue1(reader.nextString());
            } else if (name.equals(VALUE2)) {
                aliment.setValue2(reader.nextString());
            } else if (name.equals(VALUE3)) {
                aliment.setValue3(reader.nextString());
            } else if (name.equals(VALUE4)) {
                aliment.setValue4(reader.nextString());
            } else if (name.equals(ID)) {
                aliment.setId(reader.nextString());
            } else {
                reader.skipValue();
                Log.w(TAG, String.format("Entity property '%s' ignored", name));
            }
        }
        reader.endObject();
        return aliment;
    }
}
