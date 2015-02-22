package com.kandarp.launcher.now.places;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity {
    String selected, selclass;
    String classes[] = {"Current_Location", "Airport", "ATM", "Hospital", "Restaurant"};

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        selclass = classes[position];
        selected = "com.kandarp.launcher.now.places." + selclass;
        Intent i = new Intent(selected);

        startActivity(i);
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes));
    }


}
