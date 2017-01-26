package com.example.nicolab.exam;

import android.app.Application;
import android.util.Log;

import com.example.nicolab.exam.net.EntityRestClient;
import com.example.nicolab.exam.service.EntityManager;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    protected static final String EXTRA_MESSAGE = "com.example.caloriecounter.MESSAGE";
    private EntityManager entityManager;
    private EntityRestClient entityRestClient;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        entityManager = new EntityManager(this);
        entityRestClient = new EntityRestClient(this);
        entityManager.setEntityClient(entityRestClient);
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
}
