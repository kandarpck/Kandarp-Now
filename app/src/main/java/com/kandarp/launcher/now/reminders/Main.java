package com.kandarp.launcher.now.reminders;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kandarp.launcher.now.R;


public class Main extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#3F51B5")));
        }

        //Initialize variables
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.drawer_items);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.drawer_items_arrays)));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switchFragment(i);
                drawerLayout.closeDrawers();
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(getClass().getName(), "Drawer opened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.i(getClass().getName(), "Drawer closed");
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Initialize FragmentManager and switch to Upcoming fragment
        fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(R.id.content_frame) == null) {
            switchFragment(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (drawerToggle.onOptionsItemSelected(item)) || super.onOptionsItemSelected(item);
    }

    public void switchFragment(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_items_arrays)[position]);
                fragment = new Upcoming();
                break;
            case 1:
                getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_items_arrays)[position]);
                fragment = new Expired();
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, getResources().getStringArray(R.array.drawer_items_arrays)[position])
                .commit();
    }
}
