package org.mti.hip.utils;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

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

    private boolean isProduction;
    private String countryCode;

    public HttpClient(final boolean isProduction, final String countryCode) {
        client = new OkHttpClient();
        Log.d("HTTP", "START " + countryCode);
        this.isProduction = isProduction;
        this.countryCode = countryCode;
//        client.setConnectTimeout(3, TimeUnit.SECONDS);
//        client.setReadTimeout(3, TimeUnit.SECONDS);
//        client.setWriteTimeout(15, TimeUnit.SECONDS);
    }

    public String post(final String endpoint, final String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = null;
        String url = "";
        if(isProduction) {
            url = prodWebUrl + endpoint;
        } else {
            url = testWebUrl + endpoint;
        }
        request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("country", "xxx")
                .build();
        Response response;
        String responseString = null;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }

    public String get(final String endpoint) throws IOException {
        Request request = null;
        String url = "";
        if(isProduction) {
            url = prodWebUrl + endpoint;
        } else {
            url = testWebUrl + endpoint;
        }
        request = new Request.Builder()
                .url(url)
                .addHeader("country", "xxx")
                .build();
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

    public String put(final String endpoint, final String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = null;
        String url;
        if(isProduction) {
            url = prodWebUrl + endpoint;
        } else {
            url = testWebUrl + endpoint;
        }
        request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("country", "xxx")
                .build();
        Log.v("mti","request.toString() = " + request.toString());
        Response response;
        String responseString;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }
}
