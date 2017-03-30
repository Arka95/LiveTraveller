package com.example.arkabhowmik.livetraveller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Notifications extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    String uid = new String();
    RecyclerView rcNotifsView;
    ArrayList<NotificationsObject> notifications ;
    private ProgressDialog pDialog;
    boolean _hasLoadedOnce=false;

    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.f1notifications, container, false);
        pDialog = new ProgressDialog(getActivity());
        uid = getArguments().getString("uid");
        rcNotifsView = (RecyclerView) rootView.findViewById(R.id.notificationsList);
        showNotifications(uid);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);


        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ ) {
                //run your async task here since the user has just focused on your fragment
                showNotifications(uid);
                _hasLoadedOnce = true;
            }
        }
    }

    private void showNotifications(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_retnotifications";

        pDialog.setMessage("Retrieving Notifications...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NOTIFICATIONS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Notifications Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray notifiArray = jObj.getJSONArray("notifs");
                        notifications=new ArrayList<>();
                        for (int i = 0; i < notifiArray.length(); i++) {
                            JSONObject jsonObject = notifiArray.getJSONObject(i);// 0 selects the latest updated data record

                            Integer id = jsonObject.getInt("not_id");
                            String from = jsonObject.getString("from");
                            String to = jsonObject.getString("to");
                            int type = jsonObject.getInt("type");
                            NotificationsObject noti = new NotificationsObject(id, to, from, type);

                            notifications.add(noti);

                        }
                        hideDialog();
                        inflateNotificationsAdapter();
                    } else {
                        // Error in retrieval. Get the error message
                        hideDialog();
                        String errorMsg = jObj.getString("error_msg");
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
                params.put("type", "1");
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

    public void inflateNotificationsAdapter() {
        NotificationsAdapter adapter = new NotificationsAdapter(getActivity(), notifications);
        // updating listview

        rcNotifsView.setAdapter(adapter);
        rcNotifsView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

}

