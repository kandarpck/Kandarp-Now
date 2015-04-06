package com.kandarp.launcher.now.reminders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import com.melnykov.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Expired extends Fragment {

    private ListView expiredReminders;
    private FloatingActionButton fab;
    private ArrayList<HashMap<String, String>> records;
    private RemindersManager remindersManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expired, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Expired reminders list
        TextView emptyExpired = (TextView) getActivity().findViewById(R.id.expired_reminders_empty);
        expiredReminders = (ListView) getActivity().findViewById(R.id.expired_reminders);
        expiredReminders.setEmptyView(emptyExpired);
        expiredReminders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewReminder(position);
            }
        });
        populateList();

        //FloatingActionButton
        fab = (FloatingActionButton) getActivity().findViewById(R.id.expired_reminders_fab);
        fab.attachToListView(expiredReminders);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReminders();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reminder_list_sort:
                showSortMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reminder_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        remindersManager = new RemindersManager(getActivity());
    }

    public void deleteReminders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.expired_reminder_delete_message)
                .setTitle(R.string.expired_reminder_delete_title)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        remindersManager.deleteExpiredReminders();
                        populateList();
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

    public void populateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                remindersManager.checkAndSetExpired();
                records = remindersManager.getExpiredReminders();

                //Delete expired reminders if user has enabled it
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (preferences.getBoolean("key_auto_delete_expired", false)) {
                    remindersManager.deleteExpiredReminders();
                    new Handler(getActivity().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.expired_auto_delete, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //Timezones and locales
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(getActivity());
                DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());

                for (HashMap<String, String> value : records) {
                    try {
                        Date date = originalFormat.parse(value.get(RemindersManager.DATE_AND_TIME));
                        value.put("local_date_time", dateFormat.format(date) + " " + timeFormat.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter adapter = new SimpleAdapter(getActivity(), records, R.layout.card_reminder_list,
                                new String[]{"local_date_time", RemindersManager.NAME},
                                new int[]{R.id.card_reminder_list_date_time, R.id.card_reminder_list_name});
                        expiredReminders.setAdapter(adapter);
                        if (expiredReminders.getAdapter().getCount() > 0){
                            fab.setVisibility(View.VISIBLE);
                        } else {
                            fab.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    public void showSortMenu() {
        PopupMenu menu = new PopupMenu(getActivity(), getActivity().findViewById(R.id.reminder_list_sort));
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_sort_reminders, menu.getMenu());
        menu.show();
    }

    public void viewReminder(int position) {
        Intent intent = new Intent(getActivity(), Reminder.class);
        Bundle bundle = new Bundle();
        bundle.putString("reminder", records.get(position).get(RemindersManager.DATE_AND_TIME));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
