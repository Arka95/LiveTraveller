package com.example.arkabhowmik.livetraveller.app;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Arka Bhowmik on 3/12/2017.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;

    private VolleySingleton()
    {
        mRequestQueue=AppController.getInstance().getRequestQueue();
        imageLoader=new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String,Bitmap> cache=new LruCache((int)(Runtime.getRuntime().maxMemory()/1024/8));
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }
    public ImageLoader getImageLoader()
    {
        return this.imageLoader;
    }
    public static VolleySingleton getInstance(){
        if (sInstance==null)
            sInstance=new VolleySingleton();

            return sInstance;
    }

}
