package com.cheddarsecurity.cheddarsafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
/**
 * This is the singleton class that is the queue
 * for the Volley requests.
 */
public class CSSingleton {

    /** Variables **/
    @SuppressLint("StaticFieldLeak")
    private static CSSingleton instance;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    /**
     * Constructor for the class.
     *
     * @param context The context of which this class is called from.
     */
    private CSSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Gets and sets the instance if null.
     *
     * @param context The context of the activity which calls this class.
     * @return Returns the instance of the class.
     */
    public static synchronized CSSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new CSSingleton(context);
        }
        return instance;
    }

    /**
     * Gets the request queue if null, or gets it if not null.
     *
     * @return Returns the request queue.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Adds the request to the request queue.
     *
     * @param req The request to be added.
     * @param <T> The type of the request to be added.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Gets the image loader.
     *
     * @return Returns the image loader.
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}