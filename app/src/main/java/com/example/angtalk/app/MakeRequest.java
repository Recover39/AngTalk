package com.example.angtalk.app;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by ganghan-yong on 2014. 3. 24..
 */
public class MakeRequest extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        URI uri;

        try {
            uri = new URI("http://10.73.45.162:5000/");

            HttpPost httpPost = new HttpPost(uri);

            StringEntity se = new StringEntity(strings[0], HTTP.UTF_8);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(se);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

            String line;
            String result = "";

            while((line = br.readLine()) != null) {
                result += line;
            }

            return result;
        }
        catch (Exception e) {
            Log.e("ERR : ", e.toString());
        }

        return null;
    }
}