package com.ebs.android.exposure.models;


import com.ebs.android.exposure.entitlements.Entitlement;

/**
 * Created by Joao Coelho on 2017-09-07.
 */

public class EmpOfflineAsset extends EmpAsset {
    public String localMediaPath;
    public Entitlement entitlement;

    @Override
    public String getId() {
        if(entitlement != null) {
            if (entitlement.channelId != null && entitlement.programId != null) {
                return entitlement.programId + "@" + entitlement.channelId;
            }
            else if(entitlement.channelId != null) {
                return "live@" + entitlement.channelId;
            }
            else {
                return entitlement.assetId;
            }
        }
        else {
            return assetId;
        }
    }
}
