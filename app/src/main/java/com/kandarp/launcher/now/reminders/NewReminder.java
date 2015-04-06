package com.kandarp.launcher.now.reminders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class NewReminder extends ActionBarActivity implements View.OnClickListener {

    private TextView date;
    private TextView time;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Date and Time
        date = (TextView) findViewById(R.id.new_date_picker);
        time = (TextView) findViewById(R.id.new_time_picker);
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        calendar = Calendar.getInstance();
        setDateAndTime();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.new_reminder_save:
                saveReminder();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_date_picker:
                showDatePicker();
                break;
            case R.id.new_time_picker:
                showTimePicker();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_reminder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void saveReminder() {
        //Check mandatory fields are not empty
        EditText name = (EditText) findViewById(R.id.new_name);
        if (name.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check that reminder date is not in the past
        if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(this, R.string.future_only, Toast.LENGTH_SHORT).show();
            return;
        }

        //Date and Time formatting and time zone
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        RemindersManager reminders = new RemindersManager(this);
        HashMap<String, String> record = new HashMap<String, String>();
        record.put(RemindersManager.DATE_AND_TIME, sdf.format(calendar.getTime()));
        record.put(RemindersManager.NAME, name.getText().toString());
        record.put(RemindersManager.LOCATION, ((EditText) findViewById(R.id.new_location)).getText().toString());
        record.put(RemindersManager.NOTES, ((EditText) findViewById(R.id.new_notes_label)).getText().toString());
        reminders.addReminder(record);
        reminders.close();

        Toast.makeText(this, R.string.reminder_set, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void setDateAndTime() {
        //Get device locale/format
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);

        //Set the TextViews
        date.setText(dateFormat.format(calendar.getTime()));
        time.setText(timeFormat.format(calendar.getTime()));
    }

    public void showDatePicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                setDateAndTime();
            }
        };
        new DatePickerDialog(this, onDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                setDateAndTime();
            }
        };
        new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(this)).show();
    }
}
