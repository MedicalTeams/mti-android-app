package org.mti.hip.utils;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class HttpClientAsync {

    private OkHttpClient client;
    private String baseUrl;
    private static final String tallyEndpoint = "/visits/upload";

    private static final String prodWebUrl = "https://hip-app-service-prod.azurewebsites.net/hip";
    private static final String testWebUrl = "https://clinicwebapp.azurewebsites.net/hip";

    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HttpClientAsync(boolean isProduction) {
        if(isProduction) {
            baseUrl = prodWebUrl;
        } else {
            baseUrl = testWebUrl;
        }
        client = new OkHttpClient();
    }

    public void requestFacilities() {
        httpRequest(GET, baseUrl + "/facilities", "");
    }

    public void requestDiagnosis() {
        httpRequest(GET, baseUrl + "/diagnosis", "");
    }

    public void requestSupplemental() {
        httpRequest(GET, baseUrl + "/supplementals", "");
    }

    public void requestSettlement() {
        httpRequest(GET, baseUrl + "/settlements", "");
    }

    public void requestInjuryLocations() {
        httpRequest(GET, baseUrl + "/injurylocations", "");
    }

    public void requestRegistration(String deviceId) {
        httpRequest(PUT, baseUrl + "/devices/" + deviceId, "");
    }

    public void request(String url) {
        httpRequest(GET, url, "");
    }

    public abstract void getResponseString(String err, String response);

    private void httpRequest(final String action, final String url, final String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = null;
        System.out.println(url);
        if(action == POST) {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else if(action == GET) {
            request = new Request.Builder()
                    .url(url)
                    .build();
        } else if(action == PUT) {
            request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
        } else {
            Log.e("HIP", "Invalid post type.");
        }
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                getResponseString(e.toString(), null);
            }

            @Override
            public void onResponse(Response response) {
                try {
                    if (!response.isSuccessful()) {
                        getResponseString("Unexpected code " + response.code() + " " + response.body().string(), null);
                    } else {
                        getResponseString(null, response.body().string());
                    }
                }catch(IOException e) {
                    getResponseString(e.toString(), null);
                }
            }
        });
    }
}
