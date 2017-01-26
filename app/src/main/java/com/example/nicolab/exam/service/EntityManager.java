package com.example.nicolab.exam.service;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.nicolab.exam.entity.Note;
import com.example.nicolab.exam.entity.database.DatabaseSettings;
import com.example.nicolab.exam.net.EntityRestClient;
import com.example.nicolab.exam.util.Cancellable;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by nicolab on 1/25/2017.
 */
public class EntityManager extends Observable {
    public static final String TAG = EntityManager.class.getSimpleName();
    private final Context context;
    private final DatabaseSettings mDatabase;
    private EntityRestClient restClient;
    private List<Note> NoteFromDatabase;
    private String alimentsLastUpdate;
    private ConcurrentMap<String, Note> entities = new ConcurrentHashMap<>();

    public EntityManager(Context context) {
        Log.d(TAG, "constructor");
        this.context = context;
        this.mDatabase = new DatabaseSettings(context);
        this.mDatabase.getWritableDatabase();
    }

    public void setNoteClient(EntityRestClient client) {
        Log.d(TAG, "setNoteClient");
        this.restClient = client;
    }

    public Cancellable saveAsync(Note Note, boolean update, final OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "save Note async");
        return restClient.saveAsync(Note, update, new OnSuccessListener<Note>() {

                    @Override
                    public void onSuccess(Note Note) {
                        Log.d(TAG, "save Note async succedded");
                        onSuccessListener.onSuccess(Note);
                        mDatabase.save(Note);
                        notifyObservers();
                    }
                }
                , onErrorListener);
    }

    public void subscribeChangeListener() {
        //TODO: ADD socketCLient
    }

    public void unsubscribeChangeListener() {
        //TODO:
    }

    public Cancellable getEntitiesAsync(final OnSuccessListener<List<Note>> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "get aliments Async...");
        return restClient.searchAsync(alimentsLastUpdate, new OnSuccessListener<List<Note>>() {
            @Override
            public void onSuccess(List<Note> result) {
                Log.d(TAG, "get aliments async succeeded");
                Log.d(TAG, String.valueOf(result.size()));
                List<Note> ent = result;
                if (ent != null) {
                    updateCachedAliments(ent);
                } else {
                    Log.d(TAG, "Aliment list is null");
                }
                onSuccessListener.onSuccess(cachedNotesByUpdated());
                notifyObservers();
            }
        }, onErrorListener);
    }

    private void updateCachedAliments(List<Note> ent) {
        Log.d(TAG, "updateCachedAliments");
        mDatabase.deleteAll();
        entities = new ConcurrentHashMap<>();
        for (Note a : ent) {
            entities.put(a.getName(), a);
            mDatabase.save(a);
        }
        setChanged();
    }

    private List<Note> cachedNotesByUpdated() {
        List<Note> alim = new ArrayList<>(entities.values());
        Log.d(TAG, String.valueOf(alim.size()));
        Collections.sort(alim, new NoteComparator());
        return alim;
    }

    public Cancellable deleteNoteAsync(String name, final OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "delete Note async");
        return restClient.deleteAsync(name, new OnSuccessListener<Note>() {

                    @Override
                    public void onSuccess(Note aliment) {
                        Log.d(TAG, "delete aliment async succedded");
                        mDatabase.deletByName(aliment);
                        onSuccessListener.onSuccess(aliment);
                        notifyObservers();
                    }
                }
                , onErrorListener);
    }

    public Cancellable getNoteAsync(final String name, final OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "get Note async");
        return restClient.readAsync(name, new OnSuccessListener<Note>() {

                    @Override
                    public void onSuccess(Note ent) {
                        Log.d(TAG, "read aliment async succedded");
                        if (ent == null) {
                            setChanged();
                            entities.remove(name);
                        } else {
                            Log.d(TAG, "Aliment not null");
                            if (!ent.equals(entities.get(ent.getName()))) {
                                setChanged();
                                entities.put(name, ent);
                                mDatabase.save(ent);
                                Log.d(TAG, name + " fetched");
                            }
                        }
                        onSuccessListener.onSuccess(ent);
                        notifyObservers();
                    }
                }
                , onErrorListener);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public List<Note> getEntititesFromDatabase() {
        return mDatabase.getAll();
    }

    public Note getNoteFromDatabase(String name) {
        return mDatabase.getOneByName(name);
    }

    private class NoteComparator implements java.util.Comparator<Note> {
        @Override
        public int compare(Note n1, Note n2) {
            return (n1.getName().compareTo(n2.getName()));
        }
    }
}
