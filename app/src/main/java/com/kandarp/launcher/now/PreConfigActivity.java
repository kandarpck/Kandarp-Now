package com.kandarp.launcher.now;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class PreConfigActivity extends Activity {

    public static final String REG_ID = "registration_id";
    public static final String EXTRA_MESSAGE = "message";
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 30;
    static final String SERVER_URL = "http://kandarp-now.comlu.com/now/register.php";
    static final String APP_TAG = "KandarpNow_Main";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
    String SENDER_ID = "928433998648";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    private Context mContext;
    private SharedPreferences mPrefs;
    private RegisterTask mRegisterTask;

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            Log.i("Package Version", packageInfo.versionCode + "");

            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_config);
        mContext = this.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        checkPlayServices();

    }

    private void checkPlayServices() {
        // TODO Auto-generated method stub
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (resultCode == ConnectionResult.SUCCESS) {
            Log.i(APP_TAG, "Play Services Available");
            String regid = getRegistrationId(mContext);

            if (regid.length() == 0) {
                Log.i(APP_TAG, "No ID found, make register object");
                mRegisterTask = new RegisterTask();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Log.i(APP_TAG,
                            "No ID found, register for new above HONEYCOMB");
                    mRegisterTask
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.i(APP_TAG,
                            "No ID found, register for new; ELSE condition");
                    mRegisterTask.execute();
                }
            }

            gcm = GoogleCloudMessaging.getInstance(mContext);

        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
        }
    }

    private String getRegistrationId(Context context) {
        String registrationId = mPrefs.getString(REG_ID, "");
        Log.i("Getting registration ID", "Shared Preferences: "
                + registrationId);
        if (registrationId.length() == 0) {
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = mPrefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        // || isRegistrationExpired() not checking now :\
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    // not used. google rarely deletes GCM ids
    private boolean isRegistrationExpired() {
        Log.i(APP_TAG, "Registeration Expired");
        long expirationTime = mPrefs.getLong(
                PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        Log.i(APP_TAG, System.currentTimeMillis() + "");
        Log.i(APP_TAG, expirationTime + "");

        return System.currentTimeMillis() > expirationTime;
    }

    private void setRegistrationId(Context context, String regId) {
        Log.i(APP_TAG, "Setting Registeration ID in SP: " + regId);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis()
                + REGISTRATION_EXPIRY_TIME_MS;
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    public class RegisterTask extends AsyncTask<String, Integer, String> {

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(mContext);
                }

                String regid = gcm.register(SENDER_ID);

                Log.d(APP_TAG, "New Registration ID made = " + regid);
                setRegistrationId(mContext, regid);

                // get imei
                TelephonyManager telephonyManager = (TelephonyManager) mContext
                        .getSystemService(Context.TELEPHONY_SERVICE);
                final String imei = telephonyManager.getDeviceId();
                Log.i("IMEI", imei + "");

                // get name
                final String name = "Kandarp";

                // Get email account
                Account[] accounts = AccountManager.get(mContext).getAccounts();
                Log.i("Email account", accounts[0].name + "");
                String possibleEmail = accounts[0].name + "";
                if (possibleEmail != null) {
                    String serverUrl = SERVER_URL;
                    Map<String, String> user = new HashMap<String, String>();
                    user.put("regId", regid);
                    user.put("email", possibleEmail);
                    user.put("imei", imei);
                    user.put("name", name);
                    PostServer mPostServer = new PostServer(serverUrl);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        Log.i(APP_TAG, "Post to Server above HONEYCOMB");

                        mPostServer.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, user);
                    } else {
                        Log.i(APP_TAG, "Post to Server; ELSE condition");
                        mPostServer.execute(user);
                    }
                }
                return regid;
            } catch (IOException ex) {
                Log.d(APP_TAG, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String regid) {

            if (regid != null && !regid.equals(null)) {
                // Any other GCM Registration processes should start from here
                Log.d("Register Task", "Register Task completed: " + regid);

            } else {
                Log.d("Register Task", "Register Task returned no regid");

                // remove from shared preferences
                Log.i(APP_TAG, "Removing registeration ID, app version and exp time from SP: ");
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(REG_ID, "");
                editor.putInt(PROPERTY_APP_VERSION, 0);
                editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, 0);
                editor.commit();

                // show pop up
                LayoutInflater inflater3 = LayoutInflater
                        .from(PreConfigActivity.this);
                View view3 = inflater3.inflate(R.layout.error, null);
                AlertDialog.Builder builder3 = new AlertDialog.Builder(PreConfigActivity.this);
                builder3.setView(view3);
                builder3.create().show();

            }
        }
    }

    private class PostServer extends
            AsyncTask<Map<String, String>, Integer, String> {

        private String serverUrl;

        PostServer(String serverUrl) {
            this.serverUrl = serverUrl;
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            URL url;
            try {
                Log.i("Registering to URL", serverUrl);
                url = new URL(serverUrl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + serverUrl);
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params[0].entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection connGet = null;
            try {

                // offset the service not available 408 error
                Log.d(APP_TAG, "GET from Server to wake up before posting");
                connGet = (HttpURLConnection) url.openConnection();
                connGet.setRequestMethod("GET");

                // handle the response
                int status = connGet.getResponseCode();
                if (status != 200) {
                    throw new IOException("Post failed with error code "
                            + status);
                } else {
                    // Get the server response
                    BufferedReader reader;
                    reader = new BufferedReader(new InputStreamReader(
                            connGet.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    String text;
                    text = sb.toString();

                    Log.d("GET passed", status + "");
                    Log.d("HTTP RESPONSE", text);

                }
                connGet.disconnect();
            } catch (ProtocolException p) {
                p.printStackTrace();
                return "false";
            } catch (IOException e) {
                e.printStackTrace();
                return "false";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(APP_TAG, "Unknwon error");
                return "false";
            }

            HttpURLConnection conn = null;
            try {
                Log.d(APP_TAG, "Posting to Server");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                // handle the response
                int status = conn.getResponseCode();
                if (status != 200) {
                    throw new IOException("Post failed with error code "
                            + status);
                } else {
                    // Get the server response
                    BufferedReader reader;
                    reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    String text;
                    text = sb.toString();

                    Log.d("Post passed", status + "");
                    Log.d("HTTP RESPONSE", text);

                }
                conn.disconnect();
            } catch (ProtocolException p) {
                p.printStackTrace();
                return "false";
            } catch (IOException e) {
                e.printStackTrace();
                return "false";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(APP_TAG, "Unknown error");
                return "false";
            }
            return "true";
        }

        @Override
        protected void onPostExecute(String results) {

            if (results.equalsIgnoreCase("true")) {
                Log.d(APP_TAG, "Registration complete");
            } else {

                Log.i(APP_TAG, "Status: " + results);

                // remove from shared preferences
                Log.i(APP_TAG, "Removing registeration ID, app version and exp time from SP");
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(REG_ID, "");
                editor.putInt(PROPERTY_APP_VERSION, 0);
                editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, 0);
                editor.commit();

                // make pop up
                LayoutInflater inflater3 = LayoutInflater
                        .from(PreConfigActivity.this);
                View view3 = inflater3.inflate(R.layout.error, null);
                AlertDialog.Builder builder3 = new AlertDialog.Builder(PreConfigActivity.this);
                builder3.setView(view3);
                builder3.create().show();
            }
        }
    }

}
