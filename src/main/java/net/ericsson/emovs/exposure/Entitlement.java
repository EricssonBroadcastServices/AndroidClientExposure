package net.ericsson.emovs.exposure;

import java.io.Serializable;

public class Entitlement implements Serializable {
    public String assetId;
    public String channelId;
    public String programId;
    public String playToken;
    public String accountId;
    public String userToken;
    public String ownerUid;
    public String requestUrl;
    public String adParameter;
    public String mediaLocator;
    public String entitlementType;
    public String playSessionId;
    public boolean isLive;
    public String mdnRequestRouterUrl;
    public boolean timeshiftEnabled;
    public boolean ffEnabled;
    public boolean rwEnabled;
    public Integer maxBitrate;
    public Integer minBitrate;
    public int imcMode;
    public int lastViewedOffset;
    public String productId;
    public String licenseExpiration;
}
