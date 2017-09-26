package com.ebs.android.exposure.entitlements;

import com.ebs.android.exposure.entitlements.Entitlement;
import com.ebs.android.exposure.entitlements.JsonRestClient;

import net.ericsson.emovs.utilities.DeviceInfo;
import net.ericsson.emovs.utilities.Logging;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.UUID.randomUUID;

public class EntitlementsEngine {
    private final static String TAG = "EntitlementEngine";
    
    private final JsonRestClient jsonRestClient;
    private String accountStatus;
    
    public EntitlementsEngine(JsonRestClient restClient) {
        this.jsonRestClient = restClient;
    }

    private JSONObject makeDeviceJson(DeviceInfo device) throws JSONException {
        return new JSONObject()
                .put("height", device.height)
                .put("width", device.width)
                .put("model", device.model)
                .put("name", device.name)
                .put("os", device.os)
                .put("osVersion", device.osVersion)
                .put("manufacturer", device.manufacturer)
                .put("type", device.type);
    }

    private JSONObject makePlayRequestParameters(String drm, String format) throws JSONException {
        return new JSONObject()
                .put("drm", drm)
                .put("format", format);
    }

    public DateTime getServerTime() {
        JSONObject timeJson = null;
        try {
            timeJson = jsonRestClient.get("/time");
            String timeStr = timeJson.getString("iso8601");
            DateTime currentTime = ISODateTimeFormat.dateTimeParser().parseDateTime(timeStr);
            return currentTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DateTime.now();
    }

    public JSONObject getProgramInfo(DateTime now, String channelId) {
        JSONObject programJson = null;
        try {
            programJson = jsonRestClient.get("/epg/" + channelId + "?from=" + now.getMillis() + "&to=" + now.getMillis());
            return programJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateEntitlement(String assetId) throws Exception {
        try {
            JSONObject jsonObject = jsonRestClient.get("/entitlement/" + assetId + "?drm=EDRM&format=HLS");

            String status = jsonObject.optString("status", "ERROR");

            if("SUCCESS".equals(status)) {
                return true;
            }
            else if("NOT_ENTITLED".equals(status)) {
                return false;
            }

            throw new Exception(status);
        }
        catch(Exception e) {
            throw e;
        }
    }

    private Entitlement getEntitlementResponse(String assetId, String channelId, String programId, JSONObject jsonObject) throws JSONException {
        Entitlement response = new Entitlement();
        
        response.assetId = assetId;
        response.channelId = channelId;
        response.programId = programId;
        response.mediaLocator = jsonObject.getString("mediaLocator");
        response.entitlementType = jsonObject.getString("entitlementType");
        response.accountId = jsonObject.optString("accountId", null);
        response.isLive = jsonObject.optBoolean("live", false);
        response.playToken = jsonObject.getString("playToken");
        

        if(jsonObject.has("edrmConfig")) {
            JSONObject edrmConfig = jsonObject.getJSONObject("edrmConfig");
            response.userToken = edrmConfig.getString("userToken");
            response.adParameter = edrmConfig.getString("adParameter");
            response.ownerUid = edrmConfig.getString("ownerId");
            response.requestUrl = edrmConfig.getString("requestUrl");
        }

        response.playSessionId = jsonObject.optString("playSessionId", randomUUID().toString());
        response.mdnRequestRouterUrl = jsonObject.optString("mdnRequestRouterUrl", null);
        response.timeshiftEnabled = jsonObject.optBoolean("timeshiftEnabled", true);
        response.licenseExpiration = jsonObject.optString("licenseExpiration", null);

        if (jsonObject.has("minBitrate")) {
            response.minBitrate = jsonObject.optInt("minBitrate");
        }

        if (jsonObject.has("maxBitrate")) {
            response.maxBitrate = jsonObject.optInt("maxBitrate");
        }

        response.ffEnabled = jsonObject.optBoolean("ffEnabled", true);
        response.rwEnabled = jsonObject.optBoolean("rwEnabled", true);
        response.lastViewedOffset = jsonObject.optInt("lastViewedOffset", 0);
        response.productId = jsonObject.optString("productId", null);
        
        return response;
    }
    
    public Entitlement getDownloadToken(String assetId) throws Exception {
       Logging.d("getDownloadToken assetID: " + assetId);
        
        return getToken(assetId, null, null, "/download/" + assetId);
    }
    
    public Entitlement getVodPlayToken(String assetId) throws Exception {
       Logging.d("getVODPlayToken assetID: " + assetId);
        
        return getToken(assetId, null, null, "/entitlement/" + assetId + "/play");
    }
    
    public Entitlement getLivePlayToken(String channelId) throws Exception {
       Logging.d("getLivePlayToken channelID: " + channelId);
        
        return getToken(null, channelId, null, "/entitlement/channel/" + channelId + "/play");
    }

    public Entitlement getCatchupPlayToken(String channelId, String programId) throws Exception {
       Logging.d("getLivePlayToken channelID: " + channelId +"programID: "+programId );

        return getToken(null, channelId, programId, "/entitlement/channel/" + channelId + "/program/"+programId +"/play");
    }

    private Entitlement getToken(String assetId, String channelId, String programId, String relativeUrl) throws Exception {
        //JSONObject jsonObject = jsonRestClient.post(relativeUrl, makePlayRequestParameters("EDRM", "HLS"));
        JSONObject jsonObject = jsonRestClient.post(relativeUrl, makePlayRequestParameters("CENC", "DASH"));
        return getEntitlementResponse(assetId, channelId, programId, jsonObject);
    }

    private void setAccountStatus(String status) {
        this.accountStatus = status;
    }

    public String getSessionToken() {
       return jsonRestClient.getSessionToken();
    }

    public String getAccountStatus() {
        return this.accountStatus;
    }
    
    public void logout() throws Exception {
        String token = jsonRestClient.getSessionToken();
        if(token!= null && !token.isEmpty()) {
            jsonRestClient.delete("/auth/session", new JSONObject());
        }
    }
}
