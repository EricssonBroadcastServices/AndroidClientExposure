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

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class ExposureError {
    public static final ExposureError NETWORK_ERROR = new ExposureError("NETWORK_ERROR");
    public static final ExposureError DEVICE_LIMIT_EXCEEDED = new ExposureError("DEVICE_LIMIT_EXCEEDED");
    public static final ExposureError SESSION_LIMIT_EXCEEDED = new ExposureError("SESSION_LIMIT_EXCEEDED");
    public static final ExposureError UNKNOWN_DEVICE_ID = new ExposureError("UNKNOWN_DEVICE_ID");
    public static final ExposureError INVALID_JSON = new ExposureError("INVALID_JSON");
    public static final ExposureError INCORRECT_CREDENTIALS = new ExposureError("INCORRECT_CREDENTIALS");
    public static final ExposureError UNKNOWN_BUSINESS_UNIT = new ExposureError("UNKNOWN_BUSINESS_UNIT");
    public static final ExposureError NO_SESSION_TOKEN = new ExposureError("NO_SESSION_TOKEN");
    public static final ExposureError INVALID_SESSION_TOKEN = new ExposureError("INVALID_SESSION_TOKEN");
    public static final ExposureError UNKNOWN_ASSET = new ExposureError("UNKNOWN_ASSET");
    public static final ExposureError NOT_ENTITLED = new ExposureError("NOT_ENTITLED");
    public static final ExposureError DEVICE_BLOCKED = new ExposureError("DEVICE_BLOCKED");
    public static final ExposureError GEO_BLOCKED = new ExposureError("GEO_BLOCKED");
    public static final ExposureError LICENSE_EXPIRED = new ExposureError("LICENSE_EXPIRED");
    public static final ExposureError NOT_ENABLED = new ExposureError("NOT_ENABLED");
    public static final ExposureError DOWNLOAD_TOTAL_LIMIT_REACHED = new ExposureError("DOWNLOAD_TOTAL_LIMIT_REACHED");
    public static final ExposureError DOWNLOAD_ASSET_LIMIT_REACHED = new ExposureError("DOWNLOAD_ASSET_LIMIT_REACHED");
    public static final ExposureError ALREADY_DOWNLOADED = new ExposureError("ALREADY_DOWNLOADED");
    public static final ExposureError DOWNLOAD_BLOCKED = new ExposureError("DOWNLOAD_BLOCKED");
    public static final ExposureError CONCURRENT_STREAMS_LIMIT_REACHED = new ExposureError("CONCURRENT_STREAMS_LIMIT_REACHED");
    public static final ExposureError NOT_AVAILABLE_IN_FORMAT = new ExposureError("NOT_AVAILABLE_IN_FORMAT");
    public static final ExposureError FORBIDDEN = new ExposureError("FORBIDDEN");
    public static final ExposureError CONNECTION_REFUSED = new ExposureError("CONNECTION_REFUSED");
    public static final ExposureError UNKNOWN_ERROR = new ExposureError("UNKNOWN_ERROR");

    final String mMessage;

    ExposureError(String message) {
        mMessage = message;
    }

    public static ExposureError fromJson(JSONObject errorJSON) throws JSONException {
        return new ExposureError(errorJSON.getString("message"));
    }

    public String toString() {
        return mMessage;
    }

    public String toString(Context context) {
        int nameResourceID = context.getResources().getIdentifier(mMessage, "string", context.getApplicationInfo().packageName);
        if (0 == nameResourceID) {
            return mMessage;
        }else {
            return context.getString(nameResourceID);
        }
    }
}
