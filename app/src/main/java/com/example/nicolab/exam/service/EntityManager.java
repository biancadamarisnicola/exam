package com.example.nicolab.exam.service;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.nicolab.exam.entity.Entity;
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
    private List<Entity> entityFromDatabase;
    private String alimentsLastUpdate;
    private ConcurrentMap<String, Entity> entities = new ConcurrentHashMap<>();

    public EntityManager(Context context) {
        Log.d(TAG, "constructor");
        this.context = context;
        this.mDatabase = new DatabaseSettings(context);
    }

    public void setEntityClient(EntityRestClient client) {
        Log.d(TAG, "setEntityClient");
        this.restClient = client;
    }

    public Cancellable saveAsync(Entity entity, boolean update, final OnSuccessListener<Entity> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "save entity async");
        return restClient.saveAsync(entity, update, new OnSuccessListener<Entity>() {

                    @Override
                    public void onSuccess(Entity entity) {
                        Log.d(TAG, "save entity async succedded");
                        onSuccessListener.onSuccess(entity);
                        mDatabase.save(entity);
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

    public Cancellable getEntitiesAsync(final OnSuccessListener<List<Entity>> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "get aliments Async...");
        return restClient.searchAsync(alimentsLastUpdate, new OnSuccessListener<List<Entity>>() {
            @Override
            public void onSuccess(List<Entity> result) {
                Log.d(TAG, "get aliments async succeeded");
                Log.d(TAG, String.valueOf(result.size()));
                List<Entity> ent = result;
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

    private void updateCachedAliments(List<Entity> ent) {
        Log.d(TAG, "updateCachedAliments");
        mDatabase.deleteAll();
        entities = new ConcurrentHashMap<>();
        for (Entity a : ent) {
            entities.put(a.getName(), a);
            mDatabase.save(a);
        }
        setChanged();
    }

    private List<Entity> cachedNotesByUpdated() {
        List<Entity> alim = new ArrayList<>(entities.values());
        Log.d(TAG, String.valueOf(alim.size()));
        Collections.sort(alim, new EntityComparator());
        return alim;
    }

    public Cancellable deleteEntityAsync(String name, final OnSuccessListener<Entity> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "delete entity async");
        return restClient.deleteAsync(name, new OnSuccessListener<Entity>() {

                    @Override
                    public void onSuccess(Entity aliment) {
                        Log.d(TAG, "delete aliment async succedded");
                        mDatabase.deletByName(aliment);
                        onSuccessListener.onSuccess(aliment);
                        notifyObservers();
                    }
                }
                , onErrorListener);
    }

    public Cancellable getEntityAsync(final String name, final OnSuccessListener<Entity> onSuccessListener, OnErrorListener onErrorListener) {
        Log.d(TAG, "get entity async");
        return restClient.readAsync(name, new OnSuccessListener<Entity>() {

                    @Override
                    public void onSuccess(Entity ent) {
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
    public List<Entity> getEntititesFromDatabase() {
        return mDatabase.getAll();
    }

    public Entity getEntityFromDatabase(String name) {
        return mDatabase.getOneByName(name);
    }

    private class EntityComparator implements java.util.Comparator<Entity> {
        @Override
        public int compare(Entity n1, Entity n2) {
            return (n1.getName().compareTo(n2.getName()));
        }
    }
}
