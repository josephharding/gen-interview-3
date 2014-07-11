package net.josephharding.routequest.transactions;

import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Request is a collection data to be executed by a transaction manager.
 * All the information required to send the request is stored here.
 */

public class Request {

    private final String TAG = "Request";

    private final String HTTP_POST = "POST";

    private String mRequestType;

    private JSONObject mParams;

    private URL mUrl;

    private HashMap<String, String> mHeaders;

    public Request(String url, JSONObject params, HashMap<String, String> header) {
        try {
            mUrl = new URL(url);
        } catch(MalformedURLException e) {
            Log.e(TAG, e.toString());
        }
        mRequestType = HTTP_POST;
        mParams = params;
        mHeaders = header;
    }

    public URL getURL() {
        return mUrl;
    }

    public String getParams() {
        return mParams.toString();
    }

    public boolean isPost() {
        return mRequestType.equals(HTTP_POST);
    }

    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

}
