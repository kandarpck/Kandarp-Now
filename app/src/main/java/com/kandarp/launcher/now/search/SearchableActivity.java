package com.kandarp.launcher.now.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kandarp.launcher.now.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kandarp on 2/22/2015.
 */
public class SearchableActivity extends Activity {

    static final String SERVER_URL = "http://104.155.226.189/solr/collection1/select";
    private static String APP_TAG = "Searchable Activity";
    TextView server_response;
    private KandarpSearch mSearch;
    private String serverResponse = "";
    private ProgressBar waitForServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_now_web);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }

    private void doMySearch(String query) {

        server_response = (TextView) findViewById(R.id.server_response);
        waitForServer = (ProgressBar) findViewById(R.id.progressBarServer);
        waitForServer.setVisibility(View.VISIBLE);

        if (query.length() > 0) {
            Log.d("Query for search", query);
            Map<String, String> user = new HashMap<String, String>();
            user.put("q", query);
            user.put("wt", "json");
            user.put("indent", "true");

            try {
                mSearch = new KandarpSearch();
                mSearch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
            } catch (Exception e) {
                Log.d(APP_TAG, "Error while querying");
                e.printStackTrace();
                // show error message
            }

        } else {
            // show error message
            Log.i(APP_TAG, "Query Object Null. Abort Search");
        }

    }

    public class KandarpSearch extends AsyncTask<Map<String, String>, Integer, String> {

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(Map<String, String>... params) {
            URL preUrl = null;
            URL postUrl = null;
            try {
                Log.i("Registering to URL", SERVER_URL);
                preUrl = new URL(SERVER_URL);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + SERVER_URL);
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
            try {
                String body = bodyBuilder.toString().trim().replaceAll(" ", "+");
                postUrl = new URL(preUrl + "?" + body);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection connGet = null;
            try {

                Log.d(APP_TAG, "GET from data from Server" + postUrl);
                connGet = (HttpURLConnection) postUrl.openConnection();
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
                    serverResponse = sb.toString();

                    Log.d("GET passed", status + "");
                    Log.d("HTTP RESPONSE", serverResponse);

                }
                connGet.disconnect();

            } catch (ProtocolException p) {
                p.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(APP_TAG, "Unknown error");
                return "";
            }

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String response) {
            waitForServer.setVisibility(View.GONE);
            server_response.setText(response);
        }
    }
}