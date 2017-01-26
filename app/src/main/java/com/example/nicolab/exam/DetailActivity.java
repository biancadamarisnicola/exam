package com.example.nicolab.exam;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nicolab.exam.entity.Note;
import com.example.nicolab.exam.service.NetworkManager;
import com.example.nicolab.exam.util.DialogUtils;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;

/**
 * Created by nicolab on 1/25/2017.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private EditText name;
    private String id;
    private String version;
    private App myApp;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);
        name = (EditText) findViewById(R.id.Note_name);
        myApp = (App) getApplication();
        networkManager = new NetworkManager();
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(DetailFragment.Note_NAME,
                    getIntent().getStringExtra(DetailFragment.Note_NAME));
            arguments.putString(DetailFragment.NOTE_ID,
                    getIntent().getStringExtra(DetailFragment.NOTE_ID));
            arguments.putString(DetailFragment.NOTE_VERSION,
                    getIntent().getStringExtra(DetailFragment.NOTE_VERSION));
            id = getIntent().getStringExtra(DetailFragment.NOTE_ID);
            version = getIntent().getStringExtra(DetailFragment.NOTE_VERSION);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            Log.d(TAG,"__________________________________");
            Log.d(TAG, fragment.getArguments().toString());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.Note_detail_container, fragment)
                    .commit();
        }

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "save aliment");
                saveAliment(view);
            }
        });
    }

    private void saveAliment(final View view) {
        name.setError(null);

        // Store values at the time of the login attempt.
        String nameString = name.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nameString)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.d(TAG, "*****************************");
            Log.d(TAG, name.getText().toString()+" "+id+" v "+version);
               Note Note = new Note(id, name.getText().toString(), 0, Integer.valueOf(version));
            myApp.getNoteManager().saveAsync(Note,
                    new OnSuccessListener<Note>() {
                        @Override
                        public void onSuccess(final Note Note1) {
                            Log.d(TAG, "saveElement - success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Note1 != null)
                                        Log.d(TAG, Note1.toString());
                                    //mProgressView.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(view.getContext(), MasterActivity.class));
                                }
                            });
                        }
                    }, new OnErrorListener() {
                        @Override
                        public void onError(final Exception e) {
                            Log.d(TAG, "saveElement - error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showError(e);
                                }
                            });
                        }
                    }
            );
        }
    }

    protected boolean isNetworkConnected() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return mConnectivityManager.getActiveNetworkInfo() != null;

        } catch (NullPointerException e) {
            return false;

        }
    }

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
//        if (mProgressView.getVisibility() == View.VISIBLE) {
//            mProgressView.setVisibility(View.INVISIBLE);
//        }
        if (isNetworkConnected()) {
            DialogUtils.showError(this, e);
        } else {
            DialogUtils.showError(this, new Exception("You are offline. Please check your internet connection"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MasterActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
