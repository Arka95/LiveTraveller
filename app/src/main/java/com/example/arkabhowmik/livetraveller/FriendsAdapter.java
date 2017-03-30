package com.example.arkabhowmik.livetraveller;
/**
 * Created by Arka Bhowmik on 9/21/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.android.volley.toolbox.ImageLoader;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;
import com.example.arkabhowmik.livetraveller.app.VolleySingleton;

import java.util.ArrayList;

/*This is the Custom Adapter for our friendsList Recycler view
* which will display all the friends we have searched for
* */
public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<PersonLite> persons = new ArrayList();

    private Context context;
    private LayoutInflater inflater;
    ImageLoader ir;
    // create constructor to initialize context and data sent from MainActivity
    public FriendsAdapter(Context context, ArrayList<PersonLite> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.persons = data;
      this.ir = VolleySingleton.getInstance().getImageLoader();

    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friends_list_element, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        myHolder.setPosition(position);
        PersonLite current = persons.get(position);
        myHolder.textName.setText(current.getName());
        myHolder.textLoc.setText(myHolder.textLoc.getText() + current.getLat().toString() + "    " + current.getLong().toString());
        if (current.getIsOnline() != 0)
            myHolder.imageOnline.setImageResource(R.drawable.ic_isonline_true);
        else
            myHolder.imageOnline.setImageResource(R.drawable.ic_isonline);

        String imageUrl= AppConfig.URL_DP+"//"+current.getId()+".png";
        ir.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    myHolder.imageDp.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return persons.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textName;
        ImageButton imageDp;
        ImageButton imageOnline;
        TextView textLoc;
        int position;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.name);
            textLoc = (TextView) itemView.findViewById(R.id.location);
            imageOnline = (ImageButton) itemView.findViewById(R.id.ImgButIsOnline);
            imageDp = (ImageButton) itemView.findViewById(R.id.ImgButProPic);

            itemView.setOnClickListener(this);
        }

        public void setPosition(int i) {
            this.position = i;
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            PersonLite current = persons.get(position);
            Intent i = new Intent(context, FetchFriendProfile.class);
            i.putExtra("fid", current.getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }

    }

}