package com.example.arkabhowmik.livetraveller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arka Bhowmik on 2/17/2017.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<NotificationsObject> notifications = new ArrayList();
    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public NotificationsAdapter(Context context, ArrayList<NotificationsObject> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.notifications = data;

    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notifications_list_element, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        myHolder.setPosition(position);
        final NotificationsObject current = notifications.get(position);
        String description;
        switch (current.getType()) {
            case 1:
                description = current.getFrom() + " sent you a friend request. ";
                break;

            case 2:
                description = current.getFrom() + " has accepted your friend request. ";
                break;

            case 3:
                description = current.getFrom() + "has started broadcasting!";
                break;

            default:
                description = current.getFrom() + "has stopped broadcasting!";
                break;
        }

            System.out.println("banut");
        myHolder.notiDescp.setText(description);
        myHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification(current.getId());
            }
        });


    }

    // return total item from List
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void deleteNotification(final Integer nid) {
        // Tag used to cancel the request
        String tag_string_req = "req_notification";
        //pDialog.setMessage("Deleting notification ...");
        //showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NOTIFICATIONS, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(MainActivity.class.getSimpleName(), "Deletion Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                   String message= jObj.getString("error_msg");

                    Toast.makeText(context,
                            message, Toast.LENGTH_SHORT).show();
                    // Check for error node in json
                    if (error) {
                        // Error in login. Get the error message

                        Toast.makeText(context,
                               message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(MainActivity.class.getSimpleName(), "Deletion Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("not_id", nid.toString());
                params.put("type", "2");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notiDescp;
        TextView notiTime;
        ImageButton del;
        int position;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            notiDescp = (TextView) itemView.findViewById(R.id.notiDescp);
            notiTime = (TextView) itemView.findViewById(R.id.notiTime);
            del = (ImageButton) itemView.findViewById(R.id.notiDel);
            //this delete button will delete the current notification
            itemView.setOnClickListener(this);
        }

        public void setPosition(int i) {
            this.position = i;
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            NotificationsObject current = notifications.get(position);
            Intent i = new Intent(context, FetchFriendProfile.class);
            i.putExtra("fid", current.getFrom());
            context.startActivity(i);

        }

    }


}




