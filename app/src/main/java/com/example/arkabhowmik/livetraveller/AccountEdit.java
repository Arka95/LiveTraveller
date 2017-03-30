package com.example.arkabhowmik.livetraveller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;
import com.example.arkabhowmik.livetraveller.helper.SQLiteHandler;
import com.example.arkabhowmik.livetraveller.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arka Bhowmik on 2/22/2017.
 */
public class AccountEdit extends Activity implements View.OnClickListener {
    ProgressDialog pDialog;
    SessionManager session;
    ImageButton dpView;
    SQLiteHandler db;
    String uid = new String();
    String email = new String();
    public Bitmap dp;
    private int PICK_IMAGE_REQUEST = 1;
    String TAG = AccountEdit.class.getSimpleName();
    EditText editPassword, editCpassword, editCity, editState, editCountry, editFname, editLname, editUid, editDob;
    Button submit;

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editCpassword = (EditText) findViewById(R.id.editCpassword);
        editDob = (EditText) findViewById(R.id.editDob);
        editCity = (EditText) findViewById(R.id.editCity);
        editCountry = (EditText) findViewById(R.id.editCountry);
        editState = (EditText) findViewById(R.id.editState);
        editFname = (EditText) findViewById(R.id.editFname);
        editLname = (EditText) findViewById(R.id.editLname);
        dpView = (ImageButton) findViewById(R.id.updateDp);
        dpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        submit = (Button) findViewById(R.id.update);
        submit.setOnClickListener(this);

        // Session manager
        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        uid = user.get("user_id");
        email = user.get("email");
        loadAccount(uid);
    }

    @Override
    public void onClick(View v) {
        String cpassword, password, dob, fname, lname, city, country, state;

        password = editPassword.getText().toString().trim();
        cpassword = editCpassword.getText().toString().trim();
        country = editCountry.getText().toString().trim();
        city = editCity.getText().toString().trim();
        state = editState.getText().toString().trim();
        dob = editDob.getText().toString().trim();
        fname = editFname.getText().toString().trim();
        lname = editLname.getText().toString().trim();

            // Progress dialog


        if (v.getId() == R.id.update) {

            if (password.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Password Cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cpassword.equals(password) && password.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Passwords dont match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isConnected()) {
                Toast.makeText(getApplicationContext(), "Not connected to the internet!", Toast.LENGTH_SHORT).show();
            } else {

                editUser(uid, dp, password, dob, country, state, city, fname, lname);
            }

        }

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void editUser(final String uid, final Bitmap dp,
                          final String password, final String dob, final String country, final String state, final String city, final String fname, final String lname) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ACCOUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // JSONObject user = jObj.getJSONObject("user");
                        Toast.makeText(getApplicationContext(), "User successfully updated your account", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(
                                AccountEdit.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
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
                params.put("pro_pic", getStringImage(scaleDown(dp,800,true)));
                params.put("password", password);
                params.put("dob", dob);
                params.put("country", country);
                params.put("state", state);
                params.put("city", city);
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("user_id",uid);
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


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    //--------------IMAGE UPLOAD---------------------------------------------------------------------

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        System.out.println("BABE:  "+encodedImage);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                dp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                dpView.setImageBitmap(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAccount(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_formprofile";

        pDialog.setMessage("Loading Account ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Account Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        //fetch the person's details
                        JSONObject user = jObj.getJSONObject("profile");
                        editFname.setText(user.getString("first_name"));
                        editLname.setText(user.getString("last_name"));
                        editCountry.setText(user.getString("country"));
                        editCity.setText(user.getString("city"));
                        editState.setText(user.getString("state"));
                        editDob.setText(user.getString("dob"));
                        // if (!user.getString("pro_pic").isEmpty())
                        dpView.setImageResource(R.drawable.ic_dp);//user.getString("pro_pic"));
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
                params.put("type", "0");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height =  Math.round((float) ratio * realImage.getHeight());;
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}

