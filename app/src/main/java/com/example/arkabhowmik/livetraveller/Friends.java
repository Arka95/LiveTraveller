package com.example.arkabhowmik.livetraveller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
 * Created by Arka Bhowmik on 7/4/2016.
 */
public class Friends extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    String uid=new String();
    Context context;
    RecyclerView rcFriendsView;
    ArrayList<PersonLite> friends;
    private ProgressDialog pDialog;
    boolean _hasLoadedOnce=false;

    public Friends() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.f2friends, container, false);
        pDialog = new ProgressDialog(getActivity());
        uid = getArguments().getString("uid");
        rcFriendsView = (RecyclerView) rootView.findViewById(R.id.friendsList);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        //fullscreen
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);


        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_) {
                //run your async task here since the user has just focused on your fragment
                showFriends(uid);
                _hasLoadedOnce = true;
            }
        }
    }

    private void showFriends(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_rfriends";

        pDialog.setMessage("Retrieving Friendslist...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Friends Response: " + response.toString());
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
                        hideDialog();
                        inflateFriendsAdapter();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        hideDialog();
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    hideDialog();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Retrieval Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("type", "5");//showFriends
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
        if (!friends.isEmpty())
            printFriends();

        FriendsAdapter adapter = new FriendsAdapter(getActivity(), friends);
        // updating listview

        rcFriendsView.setAdapter(adapter);
        rcFriendsView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public void printFriends()
    {

        for(PersonLite it:friends)
        {
            System.out.println(it.getName() +" "+ it.getIsOnline());
        }
    }

}
