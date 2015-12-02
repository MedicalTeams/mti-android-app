package org.mti.hip.utils;

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
    public static final String tallyEndpoint = "/facilities/11/visits/upload";
    public static final String visitEndpoint = "/facilities/11/visits";
    public static final String facilitiesEndpoint = "/facilities";
    public static final String diagnosisEndpoint = "/diagnosis";
    public static final String supplementalEndpoint = "/supplementals";
    public static final String settlementEndpoint = "/settlements";
    public static final String injuryLocationsEndpoint = "/injurylocations";
    public static final String devicesEndpoint = "/devices";

    public static final String hipWebUrl = "http://clinicwebapp.azurewebsites.net/hip";

    public static final String post = "POST";
    public static final String get = "GET";
    public static final String put = "PUT";

    // TODO add constant for facility ID (this will be SET when it is selected
    // from the Centre Selection screen)
 /*
 Default Show/Hide List Operations Expand Operations
GET /facilities/{facilityId}/visits Get facility visits
POST /facilities/{facilityId}/visits Add visit
POST /facilities/{facilityId}/visits/upload Upload visits
GET /facilities
GET /facilities/{facilityId}
GET /citizenships Citizenship Lookup List
GET /diagnosis Diagnosis Lookup List
GET /supplementals

  */
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpClient() {
        client = new OkHttpClient();
    }

    public String post(final String endpoint, final String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(hipWebUrl + endpoint)
                .post(body)
                .build();
        Response response;
        String responseString = null;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        return responseString;
    }

    public String get(final String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(hipWebUrl + endpoint)
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
            throw new IOException("There was an issue with the network request");
        }
        return responseString;
    }

    public String put(final String endpoint, final String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(hipWebUrl + endpoint)
                .put(body)
                .build();
        Log.v("mti","request.toString() = " + request.toString());
        Response response;
        String responseString = null;
        response = client.newCall(request).execute();
        responseString = parseResponse(response);
        Log.v("mti", "responseString = " + responseString);
        return responseString;
    }

}
