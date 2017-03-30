package com.example.arkabhowmik.livetraveller;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arka Bhowmik on 6/30/2016.
 */


public class SearchResultsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public final String TAG=SearchResultsActivity.class.getSimpleName();
    ArrayList<PersonLite> friends;
    RecyclerView rcFriendsView;
    SwipeRefreshLayout swipeRefreshLayout;
SearchView searchView=null;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        pDialog=new ProgressDialog(getApplicationContext());
        rcFriendsView = (RecyclerView)findViewById(R.id.searchList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchResultsActivity.this.getComponentName()));
            searchView.setIconified(false);
        }

            //searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            searchFriends(query);
        }
    }


    private void searchFriends(final String search) {
        // Tag used to cancel the request
        String tag_string_req = "search_rfriends";

       // pDialog.setMessage("Searching..");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray friendsArray = jObj.getJSONArray("friends");
                        friends=new ArrayList<>();
                        for (int i = 0; i < friendsArray.length(); i++) {
                            JSONObject jsonObject = friendsArray.getJSONObject(i);// 0 selects the latest updated data record
                            String id = jsonObject.getString("user_id");
                            String name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                            int isOnline = jsonObject.getInt("is_online");
                            double lat = jsonObject.getDouble("curr_lat");
                            double longi = jsonObject.getDouble("curr_long");
                            String dp = jsonObject.getString("pro_pic");
                            PersonLite noti = new PersonLite(id, name, isOnline, lat, longi, dp);
                            friends.add(noti);
                        }

                       // hideDialog();
                        inflateFriendsAdapter();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
            //        hideDialog();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Retrieval Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
          //      hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", search);
                params.put("type", "7");//showFriends
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void inflateFriendsAdapter() {
        FriendsAdapter adapter = new FriendsAdapter(getApplicationContext(), friends);
        // updating listview
        rcFriendsView.setAdapter(adapter);
        rcFriendsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public void onRefresh() {

            if (searchView != null) {
                String query=searchView.getQuery().toString();
                searchView.clearFocus();
                searchFriends(query);
            }

    }
}
