package com.example.nicolab.exam;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nicolab.exam.entity.Entity;
import com.example.nicolab.exam.util.DialogUtils;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.view.View.VISIBLE;

/**
 * Created by nicolab on 1/25/2017.
 */
public class EditEntityActivity extends AppCompatActivity {
    private static final String TAG = EditEntityActivity.class.getSimpleName();
    private EditText name;
    private EditText value1;
    private EditText value2;
    private EditText value3;
    private EditText value4;
    private View mProgressView;
    private boolean update;

    private App myApp;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (App) getApplication();
        setContentView(R.layout.activity_edit_event);
        update = false;
        name = (EditText) findViewById(R.id.entity_name);
        value1 = (EditText) findViewById(R.id.entity_value1);
        value2 = (EditText) findViewById(R.id.entity_value2);
        value3 = (EditText) findViewById(R.id.entity_value3);
        value4 = (EditText) findViewById(R.id.entity_value4);
        String aliment = getIntent().getStringExtra("Entity");
        if (aliment != null) {
            Log.d(TAG, aliment);
            parseEntity(aliment);
            update = true;
        }
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "save aliment");
                try {
                    saveAliment(view);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "cancel edit entity");
                startActivity(new Intent(view.getContext(), MasterActivity.class));
            }
        });

        mProgressView = findViewById(R.id.save_progress);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void parseEntity(String aliment) {
        String[] elems = aliment.split(",");
        String n = elems[0].split("=")[1];
        name.setText(n.substring(1, name.length() - 1));
        String v1 = elems[1].split("=")[1];
        value1.setText(v1);
        String v2 = elems[2].split("=")[1];
        value2.setText(v2);
        String v3 = elems[3].split("=")[1];
        value3.setText(v3);
        String v4 = elems[4].split("=")[1];
        value4.setText(v4.substring(0, v4.length() - 1));
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void saveAliment(final View view) throws InterruptedException {
        name.setError(null);
        value1.setError(null);
        value2.setError(null);
        value3.setError(null);
        value4.setError(null);

        // Store values at the time of the login attempt.
        String nameString = name.getText().toString();
        String value1String = value1.getText().toString();
        String value2String = value2.getText().toString();
        String value3String = value3.getText().toString();
        String value4String = value4.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(value1String)) {
            value1.setError(getString(R.string.error_field_required));
            focusView = value1;
            cancel = true;
        }
        if (TextUtils.isEmpty(nameString)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }
        if (TextUtils.isEmpty(value2String)) {
            value2.setError(getString(R.string.error_field_required));
            focusView = value2;
            cancel = true;
        }
        if (TextUtils.isEmpty(value3String)) {
            value3.setError(getString(R.string.error_field_required));
            focusView = value3;
            cancel = true;
        }
        if (TextUtils.isEmpty(value4String)) {
            value4.setError(getString(R.string.error_field_required));
            focusView = value4;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Thread.sleep(1000);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressView.setVisibility(VISIBLE);
            Entity entity = new Entity(name.getText().toString(), value1.getText().toString(), value2.getText().toString(), value3.getText().toString(), value4.getText().toString());
            myApp.getEntityManager().saveAsync(entity, update,
                    new OnSuccessListener<Entity>() {
                        @Override
                        public void onSuccess(final Entity entity1) {
                            Log.d(TAG, "saveElement - success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (entity1 != null)
                                        Log.d(TAG, entity1.toString());
                                    mProgressView.setVisibility(View.INVISIBLE);
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

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
        if (mProgressView.getVisibility() == View.VISIBLE) {
            mProgressView.setVisibility(View.INVISIBLE);
        }
        if (isNetworkConnected()) {
            DialogUtils.showError(this, e);
        } else {
            DialogUtils.showError(this, new Exception("You are offline. Please check your internet connection"));
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("EditEvent Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
