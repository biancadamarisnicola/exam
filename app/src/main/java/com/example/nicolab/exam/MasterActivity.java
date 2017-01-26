package com.example.nicolab.exam;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nicolab.exam.entity.Entity;
import com.example.nicolab.exam.service.NetworkManager;
import com.example.nicolab.exam.util.Cancellable;
import com.example.nicolab.exam.util.DialogUtils;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;

import java.util.List;

public class MasterActivity extends AppCompatActivity implements NetworkManager.NetworkStateReceiverListener{
    private static final String TAG = MasterActivity.class.getSimpleName();
    private NetworkManager networkManager;
    private ConnectivityManager connectivityManager;
    private View contentLoadingView;
    private RecyclerView recyclerView;
    private App myApp;
    boolean activityRunning;
    private boolean entityLoaded;
    private Cancellable getEntityAsyncCall;
    private AlimentRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        myApp = (App) getApplication();
        networkManager = new NetworkManager();
        networkManager.addListener(this);
        this.registerReceiver(networkManager, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(App.EXTRA_MESSAGE);
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText(message);
        //setupToolbar();
        setupFloatingActionBar();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        if (countSensor != null) {
//            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
        this.unregisterReceiver(networkManager);
    }


    private void setupFloatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Create new aliment");
                if (isNetworkOnline()) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, EditEntityActivity.class);
                    context.startActivity(intent);
                }else{
                }
            }
        });
    }

    private void setupRecyclerView() {
        contentLoadingView = findViewById(R.id.content_loading);
        recyclerView = (RecyclerView) findViewById(R.id.aliment_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        Log.d(TAG, "onSTart");
        super.onStart();
        startGetAlimentsAsync();
        myApp.getEntityManager().subscribeChangeListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startGetAlimentsAsync() {
        if (entityLoaded) {
            Log.d(TAG, "start startGetAlimentsAsync - content already loaded, return");
            return;
        }
        showLoadingIndicator();
        if (isNetworkOnline()) {
            Log.d(TAG, "Online network");
            getEntityAsyncCall = myApp.getEntityManager().getEntitiesAsync(
                    new OnSuccessListener<List<Entity>>() {
                        @Override
                        public void onSuccess(final List<Entity> alim) {
                            Log.d(TAG, "getAlimentsAsyncCall - success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showContent(alim);
                                }
                            });
                        }
                    }, new OnErrorListener() {
                        @Override
                        public void onError(final Exception e) {
                            Log.d(TAG, "getAlimentsAsyncCall - error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showError(e);
                                }
                            });
                        }
                    }
            );
        } else {
            Log.d(TAG, "Offline network");
            final List<Entity> entitites = myApp.getEntityManager().getEntititesFromDatabase();
            Log.d(TAG, "getAlimentsFromDatabase - success");
            showContent(entitites);
        }
    }

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
        if (contentLoadingView.getVisibility() == View.VISIBLE) {
            contentLoadingView.setVisibility(View.GONE);
        }
        DialogUtils.showError(this, e);
    }

    private void showContent(List<Entity> aliments) {
        Log.d(TAG, "showContent: size "+aliments.size());
        adapter = new AlimentRecyclerViewAdapter(aliments);
        recyclerView.setAdapter(adapter);
        contentLoadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void showLoadingIndicator() {
        Log.d(TAG, "showLoadingIndicator");
        recyclerView.setVisibility(View.GONE);
        contentLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        ensureGetAlimentsAsyncCallCancelled();
        myApp.getEntityManager().unsubscribeChangeListener();
    }

    private void ensureGetAlimentsAsyncCallCancelled() {
        if (getEntityAsyncCall != null) {
            Log.d(TAG, "ensureGetAlimentsAsyncCallCancelled - cancelling the task");
            getEntityAsyncCall.cancel();
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    @Override
    public void networkAvailable() {
        Log.d(TAG, "Network connection is available");
    /* TODO: Your connection-oriented stuff here */
    }

    @Override
    public void networkUnavailable() {
        Log.d(TAG, "Network connection is unavailable");
    /* TODO: Your disconnection-oriented stuff here */
    }

    public boolean isNetworkOnline() {
        Log.d(TAG, "Check if network is online");
        TextView textView = (TextView) findViewById(R.id.activity_info_text);
        if (connectivityManager.getActiveNetworkInfo() != null){
            textView.setText("You are online");
        }else{
            textView.setText("You are offline");
        }
        return connectivityManager.getActiveNetworkInfo() != null;
    }


    //TODO:************************************************************************************************************************
    private class AlimentRecyclerViewAdapter extends RecyclerView.Adapter<AlimentRecyclerViewAdapter.ViewHolder> {

        private final List<Entity> entities;

        public AlimentRecyclerViewAdapter(List<Entity> alims) {
            entities = alims;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.entity_list_content, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder " + entities.get(position));
            holder.item = entities.get(position);
            holder.nameView.setText(entities.get(position).getName());
            //holder.contentView.setText(" - value1: " + String.valueOf(entities.get(position).getValue1()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(DetailFragment.ENTITY_NAME, holder.item.getName());
                        context.startActivity(intent);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return entities.size();
        }


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public final TextView nameView;
            public final TextView contentView;
            public Entity item;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                nameView = (TextView) view.findViewById(R.id.text_n);
                contentView = (TextView) view.findViewById(R.id.text_d);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + contentView.getText() + "'";
            }
        }
    }
}
