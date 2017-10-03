package com.ebs.android.exposure.clients.exposure;
/*
 * Copyright (c) 2017 Ericsson. All Rights Reserved
 *
 * This SOURCE CODE FILE, which has been provided by Ericsson as part
 * of an Ericsson software product for use ONLY by licensed users of the
 * product, includes CONFIDENTIAL and PROPRIETARY information of Ericsson.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF
 * THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 */

import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ebs.android.exposure.interfaces.IExposureCallback;

import org.json.JSONArray;
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
import java.net.MalformedURLException;
import java.net.URL;

public class ExposureClient {
    private final static String TAG = "ExposureClient";

    private final static String BASE_PATH = "v1/customer/%s/businessunit/%s/";
    private final static int HTTP_CONNECT_TIMEOUT = 4000;
    private final static int HTTP_READ_TIMEOUT = 10000;

    private String mExposureUrl;
    private String mCustomer;
    private String mBusinessUnit;
    private String mSessionToken;

    private static class EmpExposureClientHolder {
        private final static ExposureClient sInstance = new ExposureClient();
    }

    public static ExposureClient getInstance() {
        return EmpExposureClientHolder.sInstance;
    }

    private ExposureClient() {
    }

    public String getExposureUrl() {
        return mExposureUrl;
    }

    public void setExposureUrl(String exposureUrl) {
        mExposureUrl = exposureUrl;
    }

    public String getCustomer() {
        return mCustomer;
    }

    public void setCustomer(String customer) {
        mCustomer = customer;
    }

    public String getBusinessUnit() {
        return mBusinessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        mBusinessUnit = businessUnit;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        mSessionToken = sessionToken;
    }

    private URL getApiUrl() throws MalformedURLException {
        if (null == mExposureUrl) {
            throw new IllegalArgumentException("Exposure URL not set");
        }

        if (null == mCustomer) {
            throw new IllegalArgumentException("Customer name not set");
        }

        if (null == mBusinessUnit) {
            throw new IllegalArgumentException("Business Unit not set");
        }

        return new URL(new URL(mExposureUrl), String.format(BASE_PATH, mCustomer, mBusinessUnit));
    }

    private HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection connection = ((HttpURLConnection)url.openConnection());
        connection.setDoInput(true);
        connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(HTTP_READ_TIMEOUT);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        if (!TextUtils.isEmpty(mSessionToken)) {
            connection.setRequestProperty("Authorization", "Bearer " + mSessionToken);
        }

        return connection;
    }

    public void getAsync(String url, IExposureCallback callback) {
        try {
            URL apiUrl = new URL(getApiUrl(), "/v1/customer/" + getCustomer() + "/businessunit/" + getBusinessUnit() + (url.startsWith("/") ? url : "/" + url));
            HttpURLConnection connection = getHttpURLConnection(apiUrl);
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);

            HttpTask task = new HttpTask(connection, null, callback);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();

        } catch (IOException e) {
            Log.e(TAG, "GET error", e);
            if (null != callback) {
                callback.onCallCompleted(null, ExposureError.NETWORK_ERROR);
            }
        }
    }

    public void postSync(String url, JSONObject body, IExposureCallback callback) {
        try {
            URL apiUrl = new URL(getApiUrl(), "/v1/customer/" + getCustomer() + "/businessunit/" + getBusinessUnit() + (url.startsWith("/") ? url : "/" + url));
            if (url.contains("eventsink")) {
                apiUrl = new URL(getApiUrl(), (url.startsWith("/") ? url : "/" + url));
            }
            HttpURLConnection connection = getHttpURLConnection(apiUrl);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            HttpTask task = new HttpTask(connection, body, callback);
            task.processSync();
        }
        catch (IOException e) {
            Log.e(TAG, "POST error", e);
            if (null != callback) {
                callback.onCallCompleted(null, ExposureError.NETWORK_ERROR);
            }
        }
    }

    public void postAsync(String url, JSONObject body, IExposureCallback callback) {
        try {
            URL apiUrl = new URL(getApiUrl(), "/v1/customer/" + getCustomer() + "/businessunit/" + getBusinessUnit() + (url.startsWith("/") ? url : "/" + url));
            HttpURLConnection connection = getHttpURLConnection(apiUrl);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            HttpTask task = new HttpTask(connection, body, callback);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();

        } catch (IOException e) {
            Log.e(TAG, "POST error", e);
            if (null != callback) {
                callback.onCallCompleted(null, ExposureError.NETWORK_ERROR);
            }
        }
    }

    public void deleteAsync(String url, IExposureCallback callback) {
        try {
            URL apiUrl = new URL(getApiUrl(), url);
            HttpURLConnection connection = getHttpURLConnection(apiUrl);
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(false);

            HttpTask task = new HttpTask(connection, null, callback);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();
        } catch (IOException e) {
            Log.e(TAG, "DELETE error", e);
            if (null != callback) {
                callback.onCallCompleted(null, ExposureError.NETWORK_ERROR);
            }
        }
    }

    private class HttpTask extends AsyncTask<Void, Void, ExposureResponse> {
        private final static String TAG = "HttpTask";

        private final HttpURLConnection mURLConnection;
        private final JSONObject mBody;
        private final IExposureCallback mCallback;

        HttpTask(HttpURLConnection connection, JSONObject body, IExposureCallback callback) {
            mURLConnection = connection;
            mBody = body;
            if(mURLConnection.getDoOutput() && null == mBody) {
                throw new IllegalArgumentException("http query body expected");
            }
            mCallback = callback;
        }

        public void processSync() {
            ExposureResponse response = process();
            onPostExecute(response);
        }

        protected ExposureResponse process() {
            Log.d(TAG, "[" + this.hashCode() + "] " + mURLConnection.getRequestMethod() + " " + mURLConnection.getURL().toString());
            ExposureResponse response = new ExposureResponse();
            try {
                mURLConnection.connect();

                if (mURLConnection.getDoOutput()) {
                    OutputStream outputStream = mURLConnection.getOutputStream();
                    try {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        try {
                            if (mBody != null) {
                                writer.write(mBody.toString());
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "[" + this.hashCode() + "] Network error", ex);
                        } finally {
                            try {
                                writer.close();
                            } catch (Exception ignored) {
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "[" + this.hashCode() + "] Network error", ex);
                    } finally {
                        try {
                            outputStream.close();
                        } catch (Exception ignored) {
                        }
                    }
                }

                response.responseCode = mURLConnection.getResponseCode();

                if (mURLConnection.getDoInput()) {
                    StringBuilder strResponse = new StringBuilder();
                    InputStream responseStream = (response.responseCode != 200) ? mURLConnection.getErrorStream() : mURLConnection.getInputStream();
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "UTF-8"));
                        try {
                            String line;
                            while (null != (line = br.readLine())) {
                                strResponse.append(line);
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "[" + this.hashCode() + "] Network error", ex);
                        } finally {
                            try {
                                br.close();
                            } catch (Exception ignored) {
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "[" + this.hashCode() + "] Network error", ex);
                    } finally {
                        try {
                            responseStream.close();
                        } catch (Exception ignored) {
                        }
                    }

                    try {
                        response.responseBody = new JSONObject(strResponse.toString());
                    }
                    catch(Exception e) {
                        JSONArray responseArray = new JSONArray(strResponse.toString());
                        response.responseBody = new JSONObject().put("items", responseArray);
                    }
                }
                Log.d(TAG, "[" + this.hashCode() + "] " + response.responseCode + " " + response.responseBody);
            } catch (Exception ex) {
                Log.e(TAG, "[" + this.hashCode() + "] Network error", ex);
            }
            return response;
        }

        @Override
        protected ExposureResponse doInBackground(Void... params) {
            return process();
        }

        @Override
        protected void onPostExecute(ExposureResponse exposureResponse) {
            if (null != mCallback) {
                ExposureError error = null;
                if (200 != exposureResponse.responseCode) {
                    try {
                        if (null != exposureResponse.responseBody) {
                            error = ExposureError.fromJson(exposureResponse.responseBody);
                        } else {
                            error = ExposureError.NETWORK_ERROR;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing error JSON", e);
                        error = ExposureError.INVALID_JSON;
                    }
                }
                mCallback.onCallCompleted(exposureResponse.responseBody, error);
            }
        }
    }
}
