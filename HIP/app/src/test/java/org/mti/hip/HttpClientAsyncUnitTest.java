package org.mti.hip;

import android.util.Log;

import org.junit.Test;
import org.mti.hip.utils.HttpClientAsync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HttpClientAsyncUnitTest {
    boolean waitForResponse = false;
    String errorStr, responseStr;

    @Test
    public void checkFacilities() throws Exception {
        errorStr = "";
        responseStr = "";
        waitForResponse = true;
        HttpClientAsync http = new HttpClientAsync(true) {
            @Override
            public void getResponseString(String err, String response) {
                waitForResponse = false;
                errorStr = err;
                responseStr = response;
            }
        };
        http.requestFacilities();
        while(waitForResponse) {
            Thread.sleep(1000);
        }
        assertEquals(responseStr, "[\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Juru\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"Kabazana\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 4,\n" +
                "    \"name\": \"Kibengo\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 5,\n" +
                "    \"name\": \"Nakivale\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Rubondo\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 6,\n" +
                "    \"name\": \"Rwekubo\",\n" +
                "    \"settlement\": \"Nakivale\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 7,\n" +
                "    \"name\": \"Nshungyezi\",\n" +
                "    \"settlement\": \"Oruchinga\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 8,\n" +
                "    \"name\": \"Rulongo\",\n" +
                "    \"settlement\": \"Oruchinga\",\n" +
                "    \"country\": \"Uganda\",\n" +
                "    \"region\": \"Africa\"\n" +
                "  }\n" +
                "]");
        assertNull(errorStr);
    }

    @Test
    public void checkBadAddress() throws Exception {
        errorStr = "";
        responseStr = "";
        waitForResponse = true;
        HttpClientAsync http = new HttpClientAsync(true) {
            @Override
            public void getResponseString(String err, String response) {
                waitForResponse = false;
                errorStr = err;
                responseStr = response;
            }
        };
        waitForResponse = true;
        http.request("http://xxx.regence.com/");
        while(waitForResponse) {
            Thread.sleep(1000);
        }
        assertNull(responseStr);
        assertEquals(errorStr, "java.net.UnknownHostException: xxx.regence.com");
    }

    @Test
    public void checkRegistration() throws Exception {
        errorStr = "";
        responseStr = "";
        waitForResponse = true;
        HttpClientAsync http = new HttpClientAsync(true) {
            @Override
            public void getResponseString(String err, String response) {
                waitForResponse = false;
                errorStr = err;
                responseStr = response;
            }
        };
        http.requestRegistration("XXXX");
        while(waitForResponse) {
            Thread.sleep(1000);
        }
        assertEquals(responseStr, "{\n" +
                "  \"uuid\": \"TonyTest\",\n" +
                "  \"applicationVersion\": \"1.2.30\",\n" +
                "  \"description\": \"Device serial number last created/updated on Thu Sep 29 19:03:26 PDT 2016\",\n" +
                "  \"status\": \"I\"\n" +
                "}");
        assertNull(errorStr);
    }
}
