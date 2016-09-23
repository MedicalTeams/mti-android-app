package org.mti.hip.utils;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    private OkHttpClient client;
    public static final String getDeviceStatus = "/devices/";
    public static final String tallyEndpoint = "/visits/upload";
    public static final String facilitiesEndpoint = "/facilities";
    public static final String diagnosisEndpoint = "/diagnosis";
    public static final String supplementalEndpoint = "/supplementals";
    public static final String settlementEndpoint = "/settlements";
    public static final String injuryLocationsEndpoint = "/injurylocations";
    public static final String devicesEndpoint = "/devices";

    public static final String prodWebUrl = "https://hip-app-service-prod.azurewebsites.net/hip/";
    public static final String testWebUrl = "https://clinicwebapp.azurewebsites.net/hip";

    public static final String post = "POST";
    public static final String get = "GET";
    public static final String put = "PUT";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpClient() {
        client = new OkHttpClient();
        client.setConnectTimeout(3, TimeUnit.SECONDS);
        client.setReadTimeout(3, TimeUnit.SECONDS);
//        client.setWriteTimeout(15, TimeUnit.SECONDS);
    }

    public String post(final String endpoint, final String json, final boolean isProduction) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = null;
        if(isProduction) {
            request = new Request.Builder()
                    .url(prodWebUrl + endpoint)
                    .post(body)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(testWebUrl + endpoint)
                    .post(body)
                    .build();
        }
        Response response;
        String responseString = null;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }

    public String get(final String endpoint, final boolean isProduction) throws IOException {
        Request request = null;
        if(isProduction) {
            request = new Request.Builder()
                    .url(prodWebUrl + endpoint)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(testWebUrl + endpoint)
                    .build();
        }
        Response response;

        String responseString = null;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }

    private String parseResponse(Response response) throws IOException {
        String responseString = response.body().string();
        if (!response.isSuccessful()) {
            Log.e("parsed response error", response.code() + " " + responseString);
            throw new IOException("The networking request was not successful. Response code: " + response.code() + " " + responseString);
        }
        return responseString;
    }

    public String put(final String endpoint, final String json, final boolean isProduction) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = null;
        if(isProduction) {
            request = new Request.Builder()
                    .url(prodWebUrl + endpoint)
                    .put(body)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(testWebUrl + endpoint)
                    .put(body)
                    .build();
        }
        Log.v("mti","request.toString() = " + request.toString());
        Response response;
        String responseString;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }

}
