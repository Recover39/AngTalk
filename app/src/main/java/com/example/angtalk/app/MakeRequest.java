package com.example.angtalk.app;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ganghan-yong on 2014. 3. 24..
 */
public class MakeRequest extends AsyncTask<String, String, String> {

    public static final String TAG = "AngTok";
    public static final String SERVER_URI = "http://10.73.45.162:5000/";

    @Override
    protected String doInBackground(String... strings) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        URI uri = null;

        try {
            uri = new URI(SERVER_URI);

            HttpPost httpPost = new HttpPost(uri);

            StringEntity se = new StringEntity(strings[0]);

            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            Log.i(TAG, "StringEntity: " + se.toString());
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(se);
            //ResponseHandler responseHandler = new BasicResponseHandler();

            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStreamReader isr = new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8");

            String s = "";
            s += isr.read();
            Log.i(TAG, "HTTP CONTENT: " + s);
            Log.i(TAG, "HTTP CODE: " + httpResponse.getStatusLine().getStatusCode() + " \n"
                    + "Phrase: " + httpResponse.getStatusLine().getReasonPhrase());

            return s;
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }
}