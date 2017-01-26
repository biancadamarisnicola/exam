package com.example.nicolab.exam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by nicolab on 1/25/2017.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(DetailFragment.Note_NAME,
                    getIntent().getStringExtra(DetailFragment.Note_NAME));
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            Log.d(TAG,"__________________________________");
            Log.d(TAG, fragment.getArguments().toString());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.Note_detail_container, fragment)
                    .commit();
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
