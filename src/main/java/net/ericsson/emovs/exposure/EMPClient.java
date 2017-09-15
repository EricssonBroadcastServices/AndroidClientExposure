package net.ericsson.emovs.exposure;

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
    static final String VERSION = BuildConfig.VERSION_NAME;

    public String baseUrl;
    public String customerGroup;
    public String businessUnit;
    public String offlinePath = null;
    public String downloadsSignatureKey;
    public String downloadsSignatureExp;
    public boolean timeshiftEnabled;

    final String trackingId;

    private ImcPlaybackArguments entitlement = new ImcPlaybackArguments();
    private EntitlementsEngine entitlementsEngine;
    private final DeviceInfo deviceInfo;
    private String playSessionId;
    private JsonRestClient empApi;


    public EMPClient(Context context, String empBaseUrl, String customerGroup, String businessUnit) {
        this.deviceInfo = DeviceInfo.collect(context);
        this.trackingId = UUID.randomUUID().toString();
        this.timeshiftEnabled = true;

        setup(empBaseUrl, customerGroup, businessUnit);
    }

    public void setup(String empBaseUrl, String customerGroup, String businessUnit) {
        baseUrl = empBaseUrl;
        empApi = new JsonRestClient(empBaseUrl, customerGroup, businessUnit, null);

        this.customerGroup = customerGroup;
        this.businessUnit = businessUnit;
        entitlementsEngine = new EntitlementsEngine(empApi);
    }

    public void enableDownload(String path, String encryptionKeyPair) {
        this.offlinePath = path;
        String[] split = encryptionKeyPair.split("\\|");
        if(split.length != 2) {
            throw new RuntimeException("Incorrect key pair string. Expected format 'ENCRYPTION_KEY|ENCRYPTION_EXP'");
        }
        this.downloadsSignatureKey = split[0];
        this.downloadsSignatureExp = split[1];
    }

    public JsonRestClient getRestClient() {
        return empApi;
    }

    public void authenticate(String username, String password) throws Exception {
        authenticate(username, password, false, null);
    }

    public void authenticate(String username, String password, boolean rememberMe) throws Exception {
        authenticate(username, password, rememberMe, null);
    }

    public void authenticate(String username, String password, boolean rememberMe, String mfaCode) throws Exception {
        entitlementsEngine.authenticateLogin(deviceInfo, username, password, rememberMe, mfaCode);
    }

    public void setAuthenticationToken(String newAuthenticationToken) throws Exception {
        if (newAuthenticationToken != null && !newAuthenticationToken.isEmpty()) {
            empApi.setSessionToken(newAuthenticationToken);
            return;
        }
        throw new Exception("Authentication may not be nil or empty");
    }

    public void loginAnonymous() throws Exception {
        entitlementsEngine.anonymousLogin(deviceInfo);
    }

    public void loginFacebook(String accessToken) throws Exception {
        loginFacebook(accessToken, false);
    }

    public void loginFacebook(String accessToken, boolean rememberMe) throws Exception {
        entitlementsEngine.facebookLogin(deviceInfo, accessToken, rememberMe);
    }

    public void loginOauth(String accessToken, String oauthType) throws Exception {
        loginOauth(accessToken, oauthType, false);
    }

    public void loginOauth(String accessToken, String oauthType, boolean rememberMe) throws Exception {
        entitlementsEngine.oauthLogin(deviceInfo, accessToken, oauthType, rememberMe);
    }

    ImcPlaybackArguments getEntitlement() {
        return this.entitlement;
    }

    public String getSessionToken() {
        return empApi.getSessionToken();
    }

    public String getAccountId() {
        return empApi.getAccountId();
    }

    public boolean isAnonymous() {
        return empApi.isAnonymous();
    }

    public ImcPlaybackArguments playVod(String assetId) throws Exception {
        return getPlaybackArguments(assetId, null, null, PLAYBACK_MODE_ADAPTIVE);
    }

    public ImcPlaybackArguments playLive(String channelId) throws Exception {
        return getPlaybackArguments(null, channelId, null, PLAYBACK_MODE_LIVE);
    }

    public ImcPlaybackArguments playCatchup(String channelId, String programId) throws Exception {
        return getPlaybackArguments(null, channelId, programId, PLAYBACK_MODE_ADAPTIVE);
    }

    public ImcPlaybackArguments playDownloadedAsset(String assetId) throws Exception {
        return getPlaybackArguments(assetId, null, null, PLAYBACK_MODE_OFFLINE);
    }

    private ImcPlaybackArguments getPlaybackArguments(String assetId, String channelId, String programId, int type) throws Exception {
        try {
            // EMP-9784 Android - Enforce authorisation token for playback
            // The purpose to check authentication, because both client and backend need to know accountId.
            if (getAccountId() == null) {
                throw new EMPException(EMPException.MISSING_AUTHENTICATION_TOKEN, "Authentication Token missing");
            }
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
                    //entitlement = assetStorage.retrieveStoredEntitlements(assetId);
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

    public boolean timeshiftEnabled() {
        return this.entitlement.timeshiftEnabled;
    }

    public String getAssetId() {
        return (entitlement != null) ? entitlement.assetId : null;
    }

    public String getChannelId() {
        return (entitlement != null) ? entitlement.channelId : null;
    }

    public String getProgramId() {
        return (entitlement != null) ? entitlement.programId : null;
    }

    public String getMediaLocator() {
        return (entitlement != null) ? entitlement.mediaLocator : null;
    }

    public String getPlayToken() {
        return (entitlement != null) ? entitlement.playToken : null;
    }

    public Boolean isLiveEntitlement() {
        return entitlement != null && entitlement.isLive;
    }

    public String getSession() {
        return playSessionId;
    }

    public String getAccountStatus(){
        return entitlementsEngine.getAccountStatus();
    }

    public int getPlayMode() { return entitlement.imcMode; }


    public void logout() throws Exception {
        try {
            entitlementsEngine.logout();
        } catch (EMPInvocationError empException) {
            // TODO: handle
        } catch (Exception ex) {
            // TODO: handle
        }
    }

    EntitlementsEngine getEntitlementsEngine() {
        return this.entitlementsEngine;
    }

    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
}
