package com.ebs.android.exposure.interfaces;
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

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.entitlements.Entitlement;

import org.json.JSONObject;

public interface IEntitlementCallback {
    void onEntitlement(Entitlement entitlement);
    void onError(ExposureError error);
    String getAssetId();
    String getChannelId();
    String getProgramId();
}
