package com.example.arkabhowmik.livetraveller;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;
import com.example.arkabhowmik.livetraveller.app.VolleySingleton;
import com.example.arkabhowmik.livetraveller.helper.SQLiteHandler;
import com.example.arkabhowmik.livetraveller.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static TextView tvUName, tvUEmail;
    TabLayout tabLayout;
    ImageView loginDP;
    DrawerLayout drawer;
    String TAG=MainActivity.class.getSimpleName();
    String uid = new String();
    String email = new String();
    private ViewPager mViewPager;
    private SQLiteHandler db;
    private SessionManager session;
    private Menu menu;
    private int[] tabIcons = {
            R.drawable.ic_menu_notifications,
            R.drawable.ic_menu_friends,
            R.drawable.ic_menu_home
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        //setupTabIcons();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logout();
        }


        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        uid = user.get("user_id");
        email = user.get("email");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Preparing LTCam for Broadcast", Toast.LENGTH_SHORT).show();
                Intent cami = new Intent(getApplicationContext(), Broadcast.class);
                cami.putExtra("uid", uid);
                startActivity(cami);
            }
        });
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    //------------------------------TABS-----------------------------------------------------

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Notifications");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[0], 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Friends");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[1], 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Home");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[2], 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle b = new Bundle();
        b.putString("uid", uid);

        Fragment f1 = new Notifications();
        f1.setArguments(b);
        Fragment f2 = new Friends();
        f2.setArguments(b);
        Fragment f3 = new Home();
        f3.setArguments(b);

        adapter.addFrag(f1, "Notifications");
        adapter.addFrag(f2, "Friends");
        adapter.addFrag(f3, "Home");
        viewPager.setAdapter(adapter);
    }

    //------------------Drawer-------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        tvUName = (TextView) findViewById(R.id.tvUName);
        tvUEmail = (TextView) findViewById(R.id.tvUEmail);
        loginDP=(ImageView)findViewById(R.id.loginDP);
        tvUName.setText(uid);
        tvUEmail.setText(email);
        ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl= AppConfig.URL_DP+"//"+uid+".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                loginDP.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_camera:
                Toast.makeText(getApplicationContext(), "Preparing LTCam for Broadcast", Toast.LENGTH_SHORT).show();
                Intent cami = new Intent(getApplicationContext(), Broadcast.class);
                startActivity(cami);
                break;
            case R.id.nav_profile:
                Intent ip = new Intent(getApplicationContext(), Fetchprofile.class);
                startActivity(ip);
                break;
            case R.id.nav_account:
                Intent i = new Intent(getApplicationContext(), AccountEdit.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                Intent k = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(k);
                break;

            case R.id.nav_logout:
                logoutUser(uid);
                break;

        }

        return true;
    }

    private void logoutUser(final String uid) {
        // Tag used to cancel the request
        boolean error=false;
        String tag_string_req = "req_logout";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SESSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Logout Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),
                            jObj.getString("error_msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                logout();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Logout Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                params.put("type", "3");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
public void logout()
{
    session.setLogin(false);
    db.deleteUsers();
    // Launching the login activity
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
}
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
