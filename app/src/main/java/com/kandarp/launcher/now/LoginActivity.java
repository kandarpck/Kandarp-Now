package com.kandarp.launcher.now;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kandarp.launcher.now.gcm.PreConfigActivity;

/**
 * Created by Kandarp on 2/1/2015.
 */
public class LoginActivity extends Activity {
    public static final String REG_ID = "registration_id";
    private EditText ed1, ed2;
    private Button bt1, bt2;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        // if already signed on, directly skip the registration process
        String regid = getRegistrationId();
        if (regid.length() > 0) {
            String name = mPrefs.getString("Current_Username", "");
            String email = mPrefs.getString("Current_Email", "");
            goToConfigActivity(name, email);
        }

        ed1 = (EditText) findViewById(R.id.editText1);
        ed2 = (EditText) findViewById(R.id.editText2);
        bt1 = (Button) findViewById(R.id.button1);
        bt2 = (Button) findViewById(R.id.button2);


        // login
        bt1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!(ed1.getText().toString().equals("") || ed2.getText()
                        .toString().equals(""))) {

                    String name = ed1.getText().toString();
                    String email = ed2.getText().toString();

                    // TODO: check for username and password....
                    Log.i("Kandarp Now Login", "Storing in SP");
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("Current_Username", name);
                    editor.putString("Current_Email", email);
                    editor.commit();

                    goToConfigActivity(name, email);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter all the fields", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        // register
        bt2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            /*     Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
            */
            }
        });

    }

    private void goToConfigActivity(String name, String email) {
        Intent intent = new Intent(LoginActivity.this,
                PreConfigActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        startActivity(intent);

    }

    private String getRegistrationId() {
        String registrationId = mPrefs.getString(REG_ID, "");
        Log.i("Getting registration ID", "Shared Preferences: "
                + registrationId);
        if (registrationId.length() == 0) {
            return "";
        }
        return registrationId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

