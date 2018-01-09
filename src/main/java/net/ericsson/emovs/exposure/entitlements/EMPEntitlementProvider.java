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
import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.entitlements.Entitlement;
import net.ericsson.emovs.utilities.errors.Error;
import net.ericsson.emovs.utilities.entitlements.IEntitlementProvider;
import net.ericsson.emovs.utilities.entitlements.IEntitlementCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static java.util.UUID.randomUUID;

public class EMPEntitlementProvider implements IEntitlementProvider {
    private static final String TAG = "EMPEntitlementProvider";
    private static final String ABR_FORMAT = "DASH";
    private static final String DRM_FORMAT = "CENC";

    private static class EmpEntitlementProviderHolder {
        private final static EMPEntitlementProvider sInstance = new EMPEntitlementProvider();
    }

    public static EMPEntitlementProvider getInstance() {
        return EmpEntitlementProviderHolder.sInstance;
    }

    protected EMPEntitlementProvider() {
    }

    @Override
    public void playVod(final String assetId, final IEntitlementCallback listener) {
        getToken("/entitlement/" + assetId + "/play", listener);
    }

    @Override
    public void playLive(final String channelId, final IEntitlementCallback listener) {
        getToken("/entitlement/channel/" + channelId + "/play", listener);
    }

    @Override
    public void playCatchup(final String channelId, final String programId, final IEntitlementCallback listener) {
        getToken("/entitlement/channel/" + channelId + "/program/" + programId + "/play", listener);
    }

    public void download(final String assetId, final IEntitlementCallback listener) {
        getToken("/download/" + assetId, listener);
    }

    private void getToken(final String path, final IEntitlementCallback listener) {
        ExposureClient exposureClient = ExposureClient.getInstance();
        if (exposureClient.getSessionToken() == null) {
            listener.onError(Error.NO_SESSION_TOKEN);
            return;
        }
        JSONObject requestParams = makePlayRequestParameters(DRM_FORMAT, ABR_FORMAT);
        ExposureClient.getInstance().postAsync(path, requestParams, new IExposureCallback() {
            @Override
            public void onCallCompleted(JSONObject response, Error error) {
                parseEntitlementResponse(listener, response, error, DRM_FORMAT, ABR_FORMAT);
            }
        });
    }

    private void parseEntitlementResponse(final IEntitlementCallback callback,
                                          final JSONObject response,
                                          final Error error, String drmFormat, String abrFormat) {
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
            Entitlement entitlement = fromJson(assetId, channelId, programId, response, drmFormat, abrFormat);

            if (entitlement != null) {
                if(callback != null) {
                    callback.onEntitlement(entitlement);
                }
                return;
            }
            else if (callback != null) {
                callback.onError(Error.INVALID_JSON);
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error parsing exposure response", ex);
            if (null != callback) {
                callback.onError(Error.INVALID_JSON);
            }
        }
    }

    private Entitlement fromJson(String assetId, String channelId, String programId, JSONObject jsonObject, String drmFormat, String abrFormat) throws JSONException {
        Entitlement response = new Entitlement();

        response.assetId = assetId;
        response.channelId = channelId;
        response.programId = programId;
        response.mediaLocator = jsonObject.getString("mediaLocator");

        if (response.mediaLocator.contains(".isml")) {
            response.isUnifiedStream = true;
        }

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

        if ("CENC".equals(drmFormat) && jsonObject.has("cencConfig")) {
            JSONObject cenc = jsonObject.getJSONObject("cencConfig");
            response.licenseServerUrl = cenc.optString("com.widevine.alpha");
            // TODO: implement drmInitDataBase64 when exposure provides it
        }
        else {
            //TODO: implement when new requirements arrive
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
