package com.ebs.android.exposure.entitlements;

import net.ericsson.emovs.utilities.Logging;
import net.ericsson.emovs.utilities.ResponseCode;
import net.ericsson.emovs.utilities.exceptions.EMPInvocationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class JsonRestClient {
    private final static String TAG = "JsonRestClient";

    private final String baseUrl;
    private final String customerUrl;
    private String sessionToken;
    private boolean isAnonymous;

    public JsonRestClient(String baseUrl, String customerGroup, String businessUnit, String sessionToken) {
        this.baseUrl = baseUrl;
        this.customerUrl = format("%s/v1/customer/%s/businessunit/%s", baseUrl, customerGroup, businessUnit);
        this.sessionToken = sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public JSONObject post(String relativeUrl, JSONObject data) throws IOException, EMPInvocationError {
        return executeRequest(customerUrl + relativeUrl, "POST", data);
    }

    public JSONObject put(String relativeUrl, JSONObject data) throws IOException, EMPInvocationError {
        return executeRequest(customerUrl + relativeUrl, "PUT", data);
    }

    public JSONObject postBase(String relativeUrl, JSONObject data) throws EMPInvocationError, IOException {
        return executeRequest(baseUrl + relativeUrl, "POST", data);
    }

    public JSONObject delete(String relativeUrl, JSONObject data) throws IOException, EMPInvocationError {
        return executeRequest(customerUrl + relativeUrl, "DELETE", data);
    }

    public JSONObject get(String relativeUrl) throws IOException, EMPInvocationError {
        return executeRequest(customerUrl + relativeUrl, "GET", "");
    }

    private JSONObject executeRequest(String url, String method, JSONObject jsonData) throws IOException, EMPInvocationError {
        String dataString = (jsonData == null) ? "" : jsonData.toString();
        return executeRequest(url, method, dataString);
    }

    private JSONObject executeRequest(String url, String method, String data) throws IOException, EMPInvocationError {
        HttpURLConnection connection = getHttpConnection(url, method);

        executeRequest(connection, data);

        int responseCode = connection.getResponseCode();
        Logging.d(String.format("%s -> %d  data: %s", url, responseCode, data));

        JSONObject parsedResponse = parseResponse(responseCode, getResponse(connection, responseCode));

        Logging.d(format("%s -> %s", url, parsedResponse));

        if (responseCode != 200) {
            ResponseCode empResponseCode = lookupResponseCode(parsedResponse);
            Logging.e(format("Received unexpected error from server: %s", parsedResponse));

            throw new EMPInvocationError(responseCode, empResponseCode);
        }

        return parsedResponse;
    }

    private String getResponse(HttpURLConnection connection, int httpResponseCode) throws IOException {
        InputStream readStream = (httpResponseCode != 200) ? connection.getErrorStream() : connection.getInputStream();
        String response = readString(readStream);

        readStream.close();
        connection.disconnect();
        return response;
    }

    private void executeRequest(HttpURLConnection connection, String data) throws IOException {
        if (connection.getDoOutput()) {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            writer.write(data);
            writer.close();
            outputStream.close();
        } else {
            connection.getResponseMessage();
        }
    }

    private JSONObject parseResponse(int httpResponseCode, String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
        } catch (JSONException ignored) {
            try {
                json = new JSONObject("{ httpCode: " + httpResponseCode + " }");
            } catch (JSONException ignored1) {
                json = new JSONObject();
            }
        }
        return json;
    }

    private HttpURLConnection getHttpConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) ((new URL(url).openConnection()));

        connection.setRequestMethod(method);
        connection.setDoOutput("POST".equals(method) || "PUT".equals(method));
        connection.setDoInput(true);

        int sendTimeout = (int) SECONDS.toMillis(20);
        connection.setConnectTimeout(sendTimeout);
        connection.setReadTimeout(sendTimeout);

        connection.setRequestProperty("Content-Type", "application/json");


        connection.setRequestProperty("Accept", "application/json");

        if (sessionToken != null) {
            connection.setRequestProperty("Authorization", "Bearer " + sessionToken);
        }

        connection.connect();

        return connection;
    }

    private ResponseCode lookupResponseCode(JSONObject json) {
        int httpCode = json.optInt("httpCode", 0);
        String message = json.optString("message", "");

        for (ResponseCode candidate : ResponseCode.values()) {
            if (candidate.HttpCode == httpCode && message.equals(candidate.Code)) {
                return candidate;
            }
        }
        return ResponseCode.UNKNOWN_ERROR;
    }

    private String readString(InputStream input) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));

        StringBuilder sb;
        String line;
        sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }

    public String getAccountId() {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }
        String[] split = sessionToken.split("\\|");
        if (split.length < 9) {
            return null;
        }

        isAnonymous = Boolean.parseBoolean(split[6]);

        return split[1];
    }

    public boolean isAnonymous() {
        // no accountId means no valid authentication token, will return isAnonymous = true.
        if (getAccountId() == null) {
            return false;
        }
        return isAnonymous;
    }

    String getSessionToken() {
        return sessionToken;
    }
}
