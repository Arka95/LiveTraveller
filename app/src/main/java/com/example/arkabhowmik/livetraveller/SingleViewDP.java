package com.example.arkabhowmik.livetraveller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by kiosk on 4/3/2016.
 */
public class SingleViewDP extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleview);
        Intent i = getIntent();
        // Selected image
        String src = i.getExtras().getString("src");
        ImageView imageView = (ImageView) findViewById(R.id.SingleView);

    }
}
