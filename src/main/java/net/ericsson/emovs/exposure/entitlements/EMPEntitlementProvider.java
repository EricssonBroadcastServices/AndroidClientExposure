package net.ericsson.emovs.exposure.entitlements;

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

import android.util.Log;

import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.exposure.clients.exposure.ExposureError;
import net.ericsson.emovs.exposure.interfaces.IEntitlementCallback;
import net.ericsson.emovs.exposure.interfaces.IExposureCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static java.util.UUID.randomUUID;

public class EMPEntitlementProvider {
    private static final String TAG = "EMPEntitlementProvider";

    private static class EmpEntitlementProviderHolder {
        private final static EMPEntitlementProvider sInstance = new EMPEntitlementProvider();
    }

    public static EMPEntitlementProvider getInstance() {
        return EmpEntitlementProviderHolder.sInstance;
    }

    protected EMPEntitlementProvider() {
    }


    public void playVod(final String assetId, final IEntitlementCallback listener) {
        getToken("/entitlement/" + assetId + "/play", listener);
    }

    public void playCatchup(final String channelId, final String programId, final IEntitlementCallback listener) {
        getToken("/entitlement/channel/" + channelId + "/program/" + programId + "/play", listener);
    }

    public void download(final String assetId, final IEntitlementCallback listener) {
        getToken("/download/" + assetId, listener);
    }

    public void playLive(final String channelId, final IEntitlementCallback listener) {
        getToken("/entitlement/channel/" + channelId + "/play", listener);
    }

    private void getToken(final String path, final IEntitlementCallback listener) {
        ExposureClient exposureClient = ExposureClient.getInstance();
        if (exposureClient.getSessionToken() == null) {
            listener.onError(ExposureError.NO_SESSION_TOKEN);
            return;
        }
        ExposureClient.getInstance().postAsync(path, makePlayRequestParameters("CENC", "DASH"), new IExposureCallback() {
            @Override
            public void onCallCompleted(JSONObject response, ExposureError error) {
                parseEntitlementResponse(listener, response, error);
            }
        });
    }

    private void parseEntitlementResponse(final IEntitlementCallback callback,
                                          final JSONObject response,
                                          final ExposureError error) {
        if (error != null) {
            if (callback != null) {
                callback.onError(error);
            }
            return;
        }

        try {
            final String assetId = callback.getAssetId();
            final String channelId = callback.getChannelId();
            final String programId = callback.getProgramId();
            Entitlement entitlement = fromJson(assetId, channelId, programId, response);

            if (entitlement != null) {
                if(callback != null) {
                    callback.onEntitlement(entitlement);
                }
                return;
            }
            else if (callback != null) {
                callback.onError(ExposureError.INVALID_JSON);
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error parsing exposure response", ex);
            if (null != callback) {
                callback.onError(ExposureError.INVALID_JSON);
            }
        }
    }

    private Entitlement fromJson(String assetId, String channelId, String programId, JSONObject jsonObject) throws JSONException {
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
        response.lastViewedOffset = jsonObject.optLong("lastViewedOffset", 0);
        response.productId = jsonObject.optString("productId", null);

        return response;
    }

    private JSONObject makePlayRequestParameters(String drm, String format) {
        try {
            return new JSONObject()
                    .put("drm", drm)
                    .put("format", format);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
