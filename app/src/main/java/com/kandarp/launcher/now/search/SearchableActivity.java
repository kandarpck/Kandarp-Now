package com.kandarp.launcher.now.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.kandarp.launcher.now.R;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Kandarp on 2/22/2015.
 */
public class SearchableActivity extends Activity {


    static final String SERVER_URL = "http://104.199.150.121//solr/collection1/select";
    private static String APP_TAG = "Searchable Activity";
    List<String> content = new ArrayList<>();
    List<String> url = new ArrayList<>();
    List<String> title = new ArrayList<>();
    ListView listView;
    List<SearchDataRow> rowItems;
    private ProgressBar waitForServer;
    private TextView queryData, queryResults;

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

        waitForServer = (ProgressBar) findViewById(R.id.progressBarServer);
        waitForServer.setVisibility(View.VISIBLE);
        queryData = (TextView) findViewById(R.id.search_list_query_data);
        queryResults = (TextView) findViewById(R.id.search_list_query_number);


        if (query.length() > 0) {
            queryData.setText("Query: " + query);
            Log.d("Query for search", query);
            Map<String, String> user = new HashMap<>();
            user.put("q", query);
            user.put("wt", "json");
            user.put("indent", "true");

            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = user.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString().trim().replaceAll(" ", "%20");
            final String solrUrl = SERVER_URL + "?" + body;
            Log.d(APP_TAG, solrUrl);

            Future<JsonObject> jsonObjectFuture = Ion.with(this)
                    .load(solrUrl)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            waitForServer.setVisibility(View.GONE);

                            if (e != null) {
                                e.printStackTrace();
                                Toast.makeText(SearchableActivity.this, "Error loading", Toast.LENGTH_LONG).show();
                                return;
                            }

                            try {

                                queryResults.setText("Pages: " +
                                        result.getAsJsonObject("response")
                                                .get("numFound")
                                                .getAsString());
                                
                                /*Log.e(APP_TAG, result.getAsJsonObject("response").toString());
                                Log.e(APP_TAG, result.getAsJsonObject("response").get("numFound").getAsString());
                                Log.e(APP_TAG, result.getAsJsonObject("response").getAsJsonArray("docs").toString());
                                Log.e(APP_TAG, result.getAsJsonObject("response").getAsJsonArray("docs").get(1).toString());
                                Log.e(APP_TAG, result.getAsJsonObject("response").getAsJsonArray("docs").get(1).getAsJsonObject().get("content").toString());
                                */

                                for (int k = 0;
                                     k < result.getAsJsonObject("response").getAsJsonArray("docs").size();
                                     k++) {
                                    /*
                                    Log.w(APP_TAG, result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("content")
                                            .toString());
                                    Log.w(APP_TAG, result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("url").toString());
                                    Log.w(APP_TAG, result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("title").toString());
                                    */

                                    content.add(result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("content")
                                            .toString());
                                    url.add(result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("url").toString());

                                    title.add(result
                                            .getAsJsonObject("response")
                                            .getAsJsonArray("docs")
                                            .get(k)
                                            .getAsJsonObject()
                                            .get("title").toString());


                                } // end for
                            }//end try
                            catch (Exception ex) {
                                ex.printStackTrace();

                            }// end catch


                            try {

                                rowItems = new ArrayList<SearchDataRow>();
                                for (int i = 0; i < content.size(); i++) {
                                    SearchDataRow item = new SearchDataRow(
                                            title.get(i),
                                            content.get(i),
                                            url.get(i)
                                    );
                                    rowItems.add(item);

                                }// end for
                                listView = (ListView) findViewById(
                                        R.id.search_results);
                                SearchCustomAdapter searchCustomAdapter =
                                        new SearchCustomAdapter
                                                (getApplicationContext(), R.layout.search_now_web_list_item, rowItems);
                                AlphaInAnimationAdapter alphaInAnimationAdapter =
                                        new AlphaInAnimationAdapter(
                                                searchCustomAdapter);

                                // Assign the ListView to the AnimationAdapter and vice
                                // versa
                                alphaInAnimationAdapter.setAbsListView(listView);

                                listView.setAdapter(alphaInAnimationAdapter);


                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                                            int arg2, long arg3) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url.get(arg2).replaceAll("\"", "")));
                                        startActivity(i);

                                    }// end item click
                                });// end listener

                            }// end try
                            catch (Exception el) {
                                el.printStackTrace();

                            }// end catch

                        }// end on completed
                    });// end future callback


        } else {
            queryData.setText("Invalid Query. Try again");
            waitForServer.setVisibility(View.GONE);
            Toast.makeText(SearchableActivity.this, "Error loading", Toast.LENGTH_LONG).show();

        }
    }

}