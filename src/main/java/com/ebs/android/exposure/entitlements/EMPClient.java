package com.ebs.android.exposure.entitlements;

import android.content.Context;
import android.support.compat.BuildConfig;

import net.ericsson.emovs.utilities.DeviceInfo;
import net.ericsson.emovs.utilities.Logging;
import net.ericsson.emovs.utilities.exceptions.EMPException;
import net.ericsson.emovs.utilities.exceptions.EMPInvocationError;

import java.io.FileNotFoundException;
import java.util.UUID;

import static net.ericsson.emovs.utilities.EMPConstants.PLAYBACK_MODE_ADAPTIVE;
import static net.ericsson.emovs.utilities.EMPConstants.PLAYBACK_MODE_LIVE;
import static net.ericsson.emovs.utilities.EMPConstants.PLAYBACK_MODE_OFFLINE;

public class EMPClient {
    public static final String TAG = "EMPClient";

    public String baseUrl;
    public String customerGroup;
    public String businessUnit;

    final String trackingId;

    private Entitlement entitlement = new Entitlement();
    private EntitlementsEngine entitlementsEngine;
    private String playSessionId;
    private JsonRestClient empApi;


    public EMPClient(Context context, String empBaseUrl, String customerGroup, String businessUnit) {
        this.trackingId = UUID.randomUUID().toString();
        setup(empBaseUrl, customerGroup, businessUnit);
    }

    public void setup(String empBaseUrl, String customerGroup, String businessUnit) {
        baseUrl = empBaseUrl;
        empApi = new JsonRestClient(empBaseUrl, customerGroup, businessUnit, null);

        this.customerGroup = customerGroup;
        this.businessUnit = businessUnit;
        entitlementsEngine = new EntitlementsEngine(empApi);
    }

    public void setAuthenticationToken(String newAuthenticationToken) throws Exception {
        if (newAuthenticationToken != null && !newAuthenticationToken.isEmpty()) {
            empApi.setSessionToken(newAuthenticationToken);
            return;
        }
        throw new Exception("Authentication may not be nil or empty");
    }

    public Entitlement playVod(String assetId) throws Exception {
        return getPlaybackArguments(assetId, null, null, PLAYBACK_MODE_ADAPTIVE);
    }

    public Entitlement playLive(String channelId) throws Exception {
        return getPlaybackArguments(null, channelId, null, PLAYBACK_MODE_LIVE);
    }

    public Entitlement playCatchup(String channelId, String programId) throws Exception {
        return getPlaybackArguments(null, channelId, programId, PLAYBACK_MODE_ADAPTIVE);
    }

    public Entitlement playDownloadedAsset(String assetId) throws Exception {
        return getPlaybackArguments(assetId, null, null, PLAYBACK_MODE_OFFLINE);
    }

    private Entitlement getPlaybackArguments(String assetId, String channelId, String programId, int type) throws Exception {
        try {
            // EMP-9784 Android - Enforce authorisation token for playback
            // The purpose to check authentication, because both client and backend need to know accountId.
            playSessionId = null;

            switch (type) {
                case PLAYBACK_MODE_LIVE:
                    entitlement = entitlementsEngine.getLivePlayToken(channelId);
                    break;

                case PLAYBACK_MODE_ADAPTIVE:
                    if (programId != null) {
                        entitlement = entitlementsEngine.getCatchupPlayToken(channelId, programId);
                    } else {
                        entitlement = entitlementsEngine.getVodPlayToken(assetId);
                    }
                    break;
                case PLAYBACK_MODE_OFFLINE:
                    if (entitlement == null) {
                        throw new FileNotFoundException("Asset could not be found in the local storage");
                    }

                    entitlement.playSessionId = UUID.randomUUID().toString();
                    entitlement.entitlementType = "OFFLINE";
                    break;

                default:
                    throw new Exception("NO VALID PLAY MODE");
            }

            playSessionId = entitlement.playSessionId;

            // Special case to handle when we get a live manifest even though we asked for a vod.
            if (entitlement.isLive && type == PLAYBACK_MODE_ADAPTIVE) {
                entitlement.imcMode = PLAYBACK_MODE_LIVE;
            } else {
                entitlement.imcMode = type;
            }

            return entitlement;
        } catch (Exception ex) {
            Logging.e("getPlaybackArguments Error: \n" + ex.toString());
            throw ex;
        }
    }

}
