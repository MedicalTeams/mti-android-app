package org.mti.hip.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by r624513 on 11/4/15.
 */
public class HttpClient {

    private HttpClient instance;
    private OkHttpClient client;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpClient() {
        client = new OkHttpClient();
    }

    public void post(final String endpoint, final String json) throws IOException {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url("http://clinicwebapp.azurewebsites.net/clinic/1234" + endpoint) // endpoint for demo is "/visit"
                        .post(body)
                        .build();
                Response response = null;
                String responseString = null;
                try {
                    response = client.newCall(request).execute();
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseString;

            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("HIP HTTP", String.valueOf(s));
                super.onPostExecute(s);
            }
        }.execute();


    }

}
