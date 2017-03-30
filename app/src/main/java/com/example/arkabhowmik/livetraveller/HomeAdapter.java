package com.example.arkabhowmik.livetraveller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    ImageLoader ir;
    private Context mContext;
    private ArrayList<PersonLite> broadcasters = new ArrayList<>();
    private LayoutInflater inflater;


    public HomeAdapter(Context mContext, ArrayList<PersonLite> broadcasters) {
        this.mContext = mContext;
        this.broadcasters = broadcasters;
        this.ir = VolleySingleton.getInstance().getImageLoader();
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.home_item_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PersonLite broadcaster = broadcasters.get(position);
        holder.title.setText(broadcaster.getName());
        holder.location.setText(broadcaster.getLong().toString() + " " + broadcaster.getLat().toString());


        String imageUrl= AppConfig.URL_DP+"//"+broadcaster.getId()+".png";
        ir.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.thumbnail.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return broadcasters.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, location;
        public ImageView thumbnail, overflow;
        int position;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            location = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            view.setOnClickListener(this);
        }

        public void setPosition(int i) {
            this.position = i;
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            PersonLite current = broadcasters.get(position);
            Intent i = new Intent(mContext, FetchFriendProfile.class);
            i.putExtra("fid", current.getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);

        }
    }
}

