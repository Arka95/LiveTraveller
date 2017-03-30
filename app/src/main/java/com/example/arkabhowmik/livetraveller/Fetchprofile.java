package com.example.arkabhowmik.livetraveller;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.ImageLoader;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;
import com.example.arkabhowmik.livetraveller.app.VolleySingleton;
import com.example.arkabhowmik.livetraveller.helper.SQLiteHandler;
import com.example.arkabhowmik.livetraveller.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arka Bhowmik on 7/4/2016.
 */
/*This is a main activity . not a fragment activity. it will pop up when we want to see the details of a person*/
public class Fetchprofile extends AppCompatActivity implements LocationListener {

    private static final String TAG = Fetchprofile.class.getSimpleName();
    SessionManager session;
    SQLiteHandler db;
    GoogleMap map;
    Person p;
    ImageButton dpMed;
    String uid = new String();
    TextView tvName,tvFolcount,tvDOB,tvLat,tvLong,tvCountry,tvState,tvCity,tvId;
    Button btBroadcast;
    Button btfollower;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            /*get extras from an intent in form of bundle and then take in the other elements necessary*/
        setContentView(R.layout.layout_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        db = new SQLiteHandler(getApplicationContext());
        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");

        pDialog = new ProgressDialog(getApplicationContext());
        loadProfile(uid);
        tvId=(TextView)findViewById(R.id.tvid);
        tvCity=(TextView)findViewById(R.id.tvCity);
        tvCountry=(TextView)findViewById(R.id.tvCountry);
        tvState=(TextView)findViewById(R.id.tvState);
        tvName = (TextView) findViewById(R.id.tvName);
        tvFolcount = (TextView) findViewById(R.id.tvFolcount);
        tvDOB = (TextView) findViewById(R.id.tvDOB);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLong = (TextView) findViewById(R.id.tvLong);
        btBroadcast = (Button) findViewById(R.id.broadCast);
        btfollower = (Button) findViewById(R.id.bFollo);
        dpMed=(ImageButton)findViewById(R.id.dpMed);
        tvId.setText(uid);
        ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl= AppConfig.URL_DP+"//"+uid+".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                dpMed.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        btBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getApplicationContext(), "Preparing LTCam for Broadcast", Toast.LENGTH_SHORT).show();
                Intent cami = new Intent(getApplicationContext(), Broadcast.class);
                cami.putExtra("uid", uid);
                startActivity(cami);
                finish();
            }
        });


        ((ViewGroup) btfollower.getParent()).removeView(btfollower);// removes a view from the parent

        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                map = mapFragment.getMap();

                map.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(bestProvider);

                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
            } else {
                return;
            }

        } catch (InflateException e) {
            Toast.makeText(getApplicationContext(), "Problems inflating the view !",
                    Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Google Play Services missing !",
                    Toast.LENGTH_LONG).show();
        }

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


    @Override
    public void onLocationChanged(Location location) {

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        tvLat.setText(latitude.toString());
        tvLong.setText(longitude.toString());
        updateLocation(uid, latitude, longitude);

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
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    private void loadProfile(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_profile";

        pDialog.setMessage("Loading Profile ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
String link=new String();
                    // Check for error node in json
                    if (!error) {
                        //fetch the person's details
                        JSONObject user = jObj.getJSONObject("profile");
                        p = new Person();
                        p.setName(user.getString("first_name") + " " + user.getString("last_name"));
                        if (!user.getString("curr_lat").isEmpty() && !user.getString("curr_long").isEmpty())
                            p.setLat(user.getDouble("curr_lat"));
                        p.setLong(user.getDouble("curr_long"));
                        p.setCountry(user.getString("country"));
                        p.setCity(user.getString("city"));
                        p.setState(user.getString("state"));
                        p.setDOB(user.getString("dob"));
                        p.setIsOnline(user.getInt("is_online"));
                        if (!user.getString("pro_pic").isEmpty()){
                            p.setDp(link=AppConfig.URL_DP+user.getString("pro_pic"));
                        System.out.println(link);

                        }
                        if (p != null) {
                            tvName.setText(p.getName());
                            tvDOB.setText(tvDOB.getText()+p.getDOB());
                            tvCity.setText(tvCity.getText()+p.getCity());
                            tvState.setText(tvState.getText()+p.getState());
                            tvCountry.setText(tvCountry.getText()+p.getCountry());
                            tvLat.setText(p.getLat().toString());
                            tvLong.setText(p.getLong().toString());
                            tvFolcount.setText("Followers:" + user.getString("count"));

                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
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


    private void updateLocation(final String uid, final Double lat, final Double longi) {
        // Tag used to cancel the request
        String tag_string_req = "req_locupdate";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SESSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Session Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Location Updation Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                params.put("type", "2");
                params.put("lat", lat.toString());
                params.put("longi", longi.toString());
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
}
