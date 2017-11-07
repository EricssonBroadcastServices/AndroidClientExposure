package net.ericsson.emovs.exposure.auth;

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
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfo {
    private static final String TAG = "DeviceInfo";

    private static final double TABLET_SIZE_TRESHOLD = 7;
    private static final String FALLBACK_ID = "AndroidId";

    private Context mApplicationContext;

    private static class DeviceInfoHolder {
        private final static DeviceInfo sInstance = new DeviceInfo();
    }

    public static DeviceInfo getInstance(Context context) {
        DeviceInfoHolder.sInstance.setApplicationContext(context.getApplicationContext());
        return DeviceInfoHolder.sInstance;
    }

    private DeviceInfo() {
    }

    private void setApplicationContext(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public String getDeviceId() {
        try {
            String android_id = Settings.Secure.getString(mApplicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (android_id == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    android_id = Build.SERIAL;
                }
            }
            if (android_id == null) {
                android_id = FALLBACK_ID;
            }
            return android_id;
        }
        catch (Exception e) {
            Log.e(TAG, "Error getting device id: " + e.toString());
            return FALLBACK_ID;
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getOS() {
        return "Android";
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    private static boolean diagonalLargerThanSize(double width, double height, double diagonalTreshold) {
        Log.v(TAG, String.format("Width %f\" Height %f\" Diagonal Treshold %f\"",width, height, diagonalTreshold));
        return width*width + height*height > diagonalTreshold*diagonalTreshold;
    }

    public JSONObject getDeviceInfo() throws JSONException {
        DisplayMetrics metrics = mApplicationContext.getResources().getDisplayMetrics();
        boolean isTablet = diagonalLargerThanSize(metrics.widthPixels/metrics.xdpi,
                metrics.heightPixels/metrics.ydpi, TABLET_SIZE_TRESHOLD);

        JSONObject deviceJSON = new JSONObject();
        deviceJSON.put("height", metrics.heightPixels)
                .put("width", metrics.widthPixels)
                .put("model", getModel())
                .put("name", "")
                .put("os", getOS())
                .put("osVersion", getOSVersion())
                .put("manufacturer", getManufacturer())
                .put("deviceId", getDeviceId())
                .put("type", isTablet ? "TABLET" : "MOBILE");

        return deviceJSON;
    }
}
