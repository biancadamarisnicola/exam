package com.example.nicolab.exam.mapping;

import android.util.JsonReader;
import android.util.Log;

import com.example.nicolab.exam.entity.Note;

import java.io.IOException;

import static com.example.nicolab.exam.mapping.Api.Note.ID;
import static com.example.nicolab.exam.mapping.Api.Note.NAME;
import static com.example.nicolab.exam.mapping.Api.Note.VALUE1;
import static com.example.nicolab.exam.mapping.Api.Note.VALUE2;

/**
 * Created by bianca on 01.12.2016.
 */
public class EntityReader implements ResourceReader<Note, JsonReader>{
    private static final String TAG = EntityReader.class.getSimpleName();

    @Override
    public Note read(JsonReader reader) throws IOException {
        Note aliment = new Note();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(NAME)) {
                aliment.setName(reader.nextString());
            } else if (name.equals(VALUE1)) {
                aliment.setUpdated(reader.nextLong());
            } else if (name.equals(VALUE2)) {
                aliment.setVersion(reader.nextInt());
            } else if (name.equals(ID)) {
                aliment.setId(reader.nextString());
            } else {
                reader.skipValue();
                Log.w(TAG, String.format("Note property '%s' ignored", name));
            }
        }
        reader.endObject();
        return aliment;
    }
}
