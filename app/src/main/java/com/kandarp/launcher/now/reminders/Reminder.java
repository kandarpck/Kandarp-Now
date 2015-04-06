package com.kandarp.launcher.now.reminders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kandarp.launcher.now.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Reminder extends ActionBarActivity implements View.OnClickListener {

    private RemindersManager remindersManager = new RemindersManager(this);
    private HashMap<String, String> reminder;
    private ListView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateTime = dateFormat.parse(bundle.getString("reminder"));
            reminder = remindersManager.getReminder(dateTime);
            getSupportActionBar().setTitle(reminder.get(RemindersManager.NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        card = (ListView) findViewById(R.id.reminder);
        populateCard();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.reminder_delete:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void populateCard() {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);

        try {
            Date date = originalFormat.parse(reminder.get(RemindersManager.DATE_AND_TIME));
            reminder.put("local_date_time", dateFormat.format(date) + " " + timeFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        data.add(reminder);

        ListAdapter adapter = new SimpleAdapter(this, data, R.layout.card_reminder,
                new String[]{RemindersManager.NAME, "local_date_time"},
                new int[]{R.id.card_reminder_name, R.id.card_reminder_date_time});
        card.setAdapter(adapter);
    }

    public void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.reminder_delete_dialog_message)
                .setTitle(R.string.reminder_delete_dialog_title)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteReminder();
                    }
                })
                .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteReminder() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateAndTime = dateFormat.parse(reminder.get(RemindersManager.DATE_AND_TIME));
            remindersManager.deleteReminder(dateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        //TODO onclick events
    }
}
