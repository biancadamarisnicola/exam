package com.example.nicolab.exam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nicolab.exam.entity.Entity;
import com.example.nicolab.exam.util.Cancellable;
import com.example.nicolab.exam.util.DialogUtils;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;

/**
 * Created by nicolab on 1/25/2017.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getSimpleName();
    public static final String ENTITY_NAME = "entity_name" ;
    private Bundle arguments;
    private Entity entity;

    private App myApp;
    private LinearLayout alimentView;
    TextView alimentTextView;
    private CollapsingToolbarLayout appBarLayout;

    private FloatingActionButton editFab;
    private FloatingActionButton deleteFab;
    private ImageView warnind;
    private boolean networkOnline;
    private Cancellable entityAsync;
//    private int id = getResources().getIdentifier("@:drawable/junk_food.jpg", null, null);

    public DetailFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        myApp = (App) context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ENTITY_NAME)) {
            // In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            editFab = (FloatingActionButton) activity.findViewById(R.id.edit_fab);
            editFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d(TAG, "edit entity");
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EditEntityActivity.class);
                    intent.putExtra("Aliment", String.valueOf(entity));
                    context.startActivity(intent);
                }
            });
            deleteFab = (FloatingActionButton) activity.findViewById(R.id.delete_fab);
            deleteFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    Log.d(TAG, "delete entity");
                    entityAsync = myApp.getEntityManager().deleteEntityAsync(
                            entity.getName(),
                            new OnSuccessListener<Entity>() {

                                @Override
                                public void onSuccess(final Entity al) {
                                    Log.d(TAG, "redirect to SearchAlimentActivity");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(v.getContext(), MasterActivity.class));
                                        }
                                    });
                                }
                            }, new OnErrorListener() {

                                @Override
                                public void onError(final Exception e) {
                                    Log.d(TAG, e.toString());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogUtils.showError(getActivity(), e);
                                        }
                                    });
                                }
                            });
                }
            });
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        alimentView = (LinearLayout) rootView.findViewById(R.id.entity_fragment);
        alimentTextView = (TextView) rootView.findViewById(R.id.text_entity);
        fillAlimentDetails();
        fetchAlimentAsync();
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fetchAlimentAsync() {
        if (isNetworkConnected()) {
            Log.d(TAG, "fetch entity async");
            Log.d(TAG, "Online mode");
            entityAsync = myApp.getEntityManager().getEntityAsync(
                    getArguments().getString(ENTITY_NAME),
                    new OnSuccessListener<Entity>() {

                        @Override
                        public void onSuccess(final Entity al) {
                            Log.d(TAG, "CHECK");
                            Log.d(TAG, al.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    entity = al;
                                    fillAlimentDetails();
                                }
                            });
                        }
                    }, new OnErrorListener() {

                        @Override
                        public void onError(final Exception e) {
                            Log.d(TAG, e.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.showError(getActivity(), e);
                                }
                            });
                        }
                    });
        } else {
            Log.d(TAG, "Offline mode - fetch from DB");
            entity = myApp.getEntityManager().getEntityFromDatabase(getArguments().getString(ENTITY_NAME));
            fillAlimentDetails();
        }
    }

    protected boolean isNetworkConnected() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return mConnectivityManager.getActiveNetworkInfo() != null;

        }catch (NullPointerException e){
            return false;

        }
    }

    private void fillAlimentDetails() {
        if (entity != null) {
            Log.d(TAG, entity.toString());
            alimentTextView.setText(entity.toStringFancy());
        }else{
            Log.d(TAG, "Aliment is null");
        }

    }
}
