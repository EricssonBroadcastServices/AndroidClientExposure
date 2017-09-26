package net.ericsson.emovs.exposure;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ebs.android.exposure.models.EmpCustomer;

import net.ericsson.emovs.utilities.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joao Coelho on 15/07/2017.
 */

public class EmpPublicApi {
    private static final String TAG = "EmpPublicApi";
    private static RequestQueue mRequestQueue;
    public static Application app;
    public static String exposureApiUrl = "https://psempexposureapi.ebsd.ericsson.net";
    public static String customerId = "DevGroup";
    public static String businessUnitId = "EnigmaTV";
    public static String authorizationToken = null;
    public static Context context;

    public static EmpCustomer getCustomer() {
        return null;
    }

    public static void getFeatured(INetworkResult builder) {
        builder.ok("");
    }


    public static void getCarouselGroupById(String carouselGroupId, INetworkResult callback) {
        makeRequest(true, baseUrl() + "/carouselgroup/" + carouselGroupId, null, callback);
    }

    public static void getMainJson(INetworkResult callback) {
        makeRequest(true, baseUrl() + "/config/main.json", null, callback);
    }

    public static void autocomplete(String query, INetworkResult callback) {
        makeRequest(true, baseUrl() + "/content/search/autocomplete/" + query, null, callback);
    }

    public static void validateSession(INetworkResult callback) {
        makeJsonRequest(true, baseUrl() + "/auth/session", null, callback);
    }

    public static void getAssets(String endpoint, INetworkResult builder) {
        String url = baseUrl() + endpoint;
        makeRequestGet(url, null, builder);
    }

    public static void getSeries(INetworkResult builder) {
        String url = baseUrl() + "/content/season?includeEpisodes=true";
        makeRequestGet(url, null, builder);
    }

    public static void getEpg(String channelId, INetworkResult builder) {
        long timeBox = 1000 * 60 * 60 * 24 * 2; // 5 days
        long nowMs = System.currentTimeMillis();
        long from = nowMs - timeBox;
        long to = nowMs + timeBox;
        String url = baseUrl() + "/epg/" + channelId + "?from=" + from + "&to=" + to;
        makeRequestGet(url, null, builder);
    }

    public static void getChannels(INetworkResult empChannelsBuilder) {
        String url = baseUrl() + "/content/asset" + "?fieldSet=ALL&&includeUserData=true&pageNumber=1&sort=originalTitle&pageSize=100&onlyPublished=true&assetType=TV_CHANNEL";
        makeRequestGet(url, null, empChannelsBuilder);
    }

    public static void login(DeviceInfo device, String username, String password, String mfaCode, INetworkResult callback) {
        boolean is2factor = mfaCode != null && !mfaCode.equals("");
        String endpoint = is2factor ? "/auth/twofactorlogin" : "/auth/login";
        JSONObject params = new JSONObject();
        try {
            params.put("device", makeDeviceArguments(device));
            params.put("deviceId", device.deviceId);
            params.put("rememberMe", "false");
            params.put("username", username);
            params.put("password", password);
            if(is2factor) {
                params.put("totp", mfaCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        makeJsonRequest(false, baseUrl() + endpoint, params, callback);
    }

    private static void makeRequestGet(String url, final HashMap<String, String> params, final INetworkResult result) {
        makeRequest(true, url, params, result);
    }

    private static void makeRequestPost(String url, final HashMap<String, String> params, final INetworkResult result) {
        makeRequest(false, url, params, result);
    }

    private static void makeRequest(boolean isGet, String url, final HashMap<String, String> params, final INetworkResult result) {
        VolleyLog.DEBUG = true;
        StringRequest strReq1 = new StringRequest(isGet ? Request.Method.GET : Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                result.ok(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                //Toast.makeText(context, "Error: " + message, Toast.LENGTH_LONG).show();
                result.error(message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(authorizationToken != null) {
                    params.put("Authorization", "Bearer " + authorizationToken);
                }
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                if(params == null) {
                   return new HashMap<>();
                }
                return params;
            }
        };

        Log.d("login Request: ", strReq1.toString());
        addToRequestQueue(strReq1, "");
    }

    private static void makeJsonRequest(boolean isGet, String url, JSONObject params, final INetworkResult result) {
        VolleyLog.DEBUG = true;
        JsonObjectRequest strReq1 = new JsonObjectRequest(isGet ? Request.Method.GET : Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                result.ok(response.toString());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof NoConnectionError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }

                        //Toast.makeText(context, "Error: " + message, Toast.LENGTH_LONG).show();
                        result.error(message);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(authorizationToken != null) {
                    params.put("Authorization", "Bearer " + authorizationToken);
                }
                params.put("Accept", "application/json");
                return params;
            }
        };

        Log.d("login Request: ", strReq1.toString());
        addToRequestQueue(strReq1, "");
    }

    private static String baseUrl() {
        return exposureApiUrl + "/v1/customer/" + customerId + "/businessunit/" + businessUnitId;
    }

    private static JSONObject makeDeviceArguments(DeviceInfo device) {
        try {
            return new JSONObject()
                    .put("height", device.height)
                    .put("width", device.width)
                    .put("model", device.model)
                    .put("name", device.name)
                    .put("os", device.os)
                    .put("osVersion", device.osVersion)
                    .put("manufacturer", device.manufacturer)
                    .put("type", device.type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(app);
        }

        return mRequestQueue;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

}
