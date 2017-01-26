package com.example.nicolab.exam;

import android.app.Application;
import android.util.Log;

import com.example.nicolab.exam.net.EntityRestClient;
import com.example.nicolab.exam.service.EntityManager;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    protected static final String EXTRA_MESSAGE = "com.example.caloriecounter.MESSAGE";
    private EntityManager NoteManager;
    private EntityRestClient NoteRestClient;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        NoteManager = new EntityManager(this);
        NoteRestClient = new EntityRestClient(this);
        NoteManager.setNoteClient(NoteRestClient);
    }


    public EntityManager getNoteManager() {
        return NoteManager;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
}
