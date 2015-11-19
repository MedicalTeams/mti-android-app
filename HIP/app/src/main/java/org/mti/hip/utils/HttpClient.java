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
    public static final String tallyEndpoint = "/visits/upload";
    public static final String visitEndpoint = "/facilities/1234/visits";
    public static final String facilitiesEndpoint = "/facilities";
    // TODO add constant for facility ID (this will be SET when it is selected
    // from the Facility Selection screen)

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
                        .url("http://clinicwebapp.azurewebsites.net/hip" + endpoint)
                        .post(body)
                        .build();
                Response response;
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

    public String get(final String endpoint) throws IOException {
        final String[] responseString = {null};
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                Request request = new Request.Builder()
                        .url("http://clinicwebapp.azurewebsites.net/hip" + endpoint)
                        .build();
                Response response;
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
                responseString[0] = s;
                super.onPostExecute(s);
            }
        }.execute();


        return responseString[0];
    }
}
