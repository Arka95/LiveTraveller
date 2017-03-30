package com.example.arkabhowmik.livetraveller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;
import com.example.arkabhowmik.livetraveller.app.VolleySingleton;
import com.example.arkabhowmik.livetraveller.helper.SQLiteHandler;
import com.example.arkabhowmik.livetraveller.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arka Bhowmik on 2/15/2017.
 * TODO:workthrough deleting a friend request and rejecting / accepting one
 */
public class FetchFriendProfile extends Activity implements View.OnClickListener {
    private static final String TAG = FetchFriendProfile.class.getSimpleName();
    Person p;
    SessionManager session;
    SQLiteHandler db;
    String uid = new String();
    String fid = new String();
    TextView tvName,tvFolcount,tvDOB,tvLat,tvLong,tvCountry,tvState,tvCity,tvId;
    ImageButton dpMed;
    String type = "1";
    Button btBroadcast;
    GoogleMap map;
    Button btfollower;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile2);

        db = new SQLiteHandler(getApplicationContext());
        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        fid = getIntent().getStringExtra("fid");
        tvCity=(TextView)findViewById(R.id.tvCity);
        tvCountry=(TextView)findViewById(R.id.tvCountry);
        tvState=(TextView)findViewById(R.id.tvState);
        tvId=(TextView)findViewById(R.id.tvid);
        tvName = (TextView) findViewById(R.id.tvName);
        tvFolcount = (TextView) findViewById(R.id.tvFolcount);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLong = (TextView) findViewById(R.id.tvLong);
        tvDOB = (TextView) findViewById(R.id.tvDOB);
        btBroadcast = (Button) findViewById(R.id.broadCast);
        btfollower = (Button) findViewById(R.id.bFollo);
        dpMed=(ImageButton)findViewById(R.id.dpMed);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog = new ProgressDialog(FetchFriendProfile.this);
        tvId.setText(fid);
        //Dynamically determining the button text based on friendship
        //adding functionality to the button
        com.android.volley.toolbox.ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl= AppConfig.URL_DP+"//"+fid+".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                dpMed.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });




        btfollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String typ = type;
                sendFriendRequest(uid, fid, typ);
            }
        });

        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map = mapFragment.getMap();

            }

        } catch (InflateException e) {
            Toast.makeText(getApplicationContext(), "Problems inflating the view !",
                    Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Google Play Services missing !",
                    Toast.LENGTH_LONG).show();
        }

    loadProfile(uid,fid);
    }




    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_search) {
            Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    private void sendFriendRequest(final String uid, final String fid, final String type) {
        // Tag used to cancel the request
        String tag_string_req = "req_friend";

        pDialog.setMessage("Sending Request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String errorMsg = jObj.getString("message");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        // Launch login activity
                        Intent intent = new Intent(
                                FetchFriendProfile.this,
                                FetchFriendProfile.class);
                        intent.putExtra("fid", fid);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("friend_id", fid);
                params.put("type", type);
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

    private void loadProfile(final String uid, final String fid) {
        // Tag used to cancel the request
        String tag_string_req = "req_fprofile";
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONObject user = jObj.getJSONObject("profile");
                        p = new Person();
                        p.setName(user.getString("first_name") + " " + user.getString("last_name"));
                        p.setLat(user.getDouble("curr_lat"));
                        p.setLong(user.getDouble("curr_long"));
                        p.setCountry(user.getString("country"));
                        p.setCity(user.getString("city"));
                        p.setState(user.getString("state"));
                        p.setDOB(user.getString("dob"));
                        p.setIsOnline(user.getInt("is_online"));
                        if(p.getIsOnline()==0)
                        {
                            p.setLat(user.getDouble("last_lat"));
                            p.setLong(user.getDouble("last_long"));
                        }
                        else
                        {
                            p.setLat(user.getDouble("curr_lat"));
                            p.setLong(user.getDouble("curr_long"));
                        }
                        p.setIsFriend(user.getInt("is_friend"));
                        if (!user.getString("pro_pic").isEmpty())
                            p.setDp(user.getString("pro_pic"));

                        if (p!=null) {

                            tvName.setText(p.getName());
                            tvDOB.setText(tvDOB.getText()+p.getDOB());
                            tvCity.setText(tvCity.getText()+p.getCity());
                            tvState.setText(tvState.getText()+p.getState());
                            System.out.println(user.getString("country"));
                            tvCountry.setText(tvCountry.getText()+p.getCountry());
                            tvFolcount.setText("Followers:"+user.getString("count"));

                            if (p.getisFriend() == 0) {
                                btfollower.setText("ADD FRIEND");//he is not a friend yet
                                type = "1";
                                ((ViewGroup) btBroadcast.getParent()).removeView(btBroadcast);
                            } else if (p.getisFriend() == 1) {
                                btfollower.setText("ACCEPT REQUEST");// you have a new crush
                                type = "2";
                              btBroadcast.setText("DELETE REQUEST");
                                btBroadcast.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sendFriendRequest(uid, fid, "3");
                                    }
                                });

                            } else if (p.getisFriend() == 2) {
                                btfollower.setText("UNFOLLOW"); // friend turned enemy or ate your food
                                type = "4";
                                if (p.getIsOnline() != 2) {
                                    ((ViewGroup) btBroadcast.getParent()).removeView(btBroadcast);
                                }
                                else
                                {
                                    btBroadcast.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent cami = new Intent(getApplicationContext(), VideoActivity.class);
                                            cami.putExtra("uid", uid);
                                            startActivity(cami);
                                            finish();
                                        }
                                    });
                                }
                            }

                            //Location update
                            LatLng loc = new LatLng(p.getLat(), p.getLong());
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                map.setMyLocationEnabled(false);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                                map.addMarker(new MarkerOptions()
                                        .title(p.getId()+"'s location")
                                        .position(loc));
                                tvLat.setText(p.getLat().toString());
                                tvLong.setText(p.getLong().toString());
                            }

                        } else
                            System.out.println("Error. Person is still null");
                       hideDialog();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("fid", fid);
                params.put("type", "2");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
