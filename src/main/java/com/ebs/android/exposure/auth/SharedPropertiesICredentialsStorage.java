package com.ebs.android.exposure.auth;/*
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
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.ebs.android.exposure.interfaces.ICredentialsStorageProvider;

import net.ericsson.emovs.utilities.ContextRegistry;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class SharedPropertiesICredentialsStorage implements ICredentialsStorageProvider {
    private static final String TAG = "EMPCredentialsProvider";

    private static final String API_URL = "api";
    private static final String CUSTOMER = "cu";
    private static final String BUSINESS_UNIT = "bu";
    private static final String CREDENTIALS = "credentials";

    private SharedPreferences mSharedPreferences;
    private Credentials mCredentials;
    private Context mContext;

    private static class CredentialsStoreHolder {
        private final static SharedPropertiesICredentialsStorage sInstance = new SharedPropertiesICredentialsStorage();
    }

    public static SharedPropertiesICredentialsStorage getInstance() {
        CredentialsStoreHolder.sInstance.setApplicationContext(ContextRegistry.get());
        return CredentialsStoreHolder.sInstance;
    }

    private SharedPropertiesICredentialsStorage() {
    }

    private void setApplicationContext(Context applicationContext) {
        mContext = applicationContext;
        init();
    }

    public void init() {
        mSharedPreferences = mContext.getSharedPreferences("EMPCredentials", MODE_PRIVATE);
        String credentials = mSharedPreferences.getString(CREDENTIALS, "");
        if(!TextUtils.isEmpty(credentials)) {
            try {
                mCredentials = Credentials.fromJSON(new JSONObject(credentials));
            } catch (Exception ex) {
                Log.e(TAG, "Error while loading stored credentials", ex);
            }
        }
    }

    public Credentials getCredentials() {
        return mCredentials;
    }

    public String getExposureUrl() {
        return mSharedPreferences.getString(API_URL, "");
    }

    public String getCustomer() {
        return mSharedPreferences.getString(CUSTOMER, "");
    }

    public String getBusinessUnit() {
        return mSharedPreferences.getString(BUSINESS_UNIT, "");
    }

    @Override
    public void storeCredentials(String exposureUrl, String customer, String businessUnit, Credentials credentials) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(API_URL, exposureUrl);
        editor.putString(CUSTOMER, customer);
        editor.putString(BUSINESS_UNIT, businessUnit);
        if (credentials != null) {
            editor.putString(CREDENTIALS, credentials.toString());
        }
        editor.commit();
        //editor.apply();
        mCredentials = credentials;
    }

    public void deleteCredentials() {
        mCredentials = null;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(API_URL);
        editor.remove(CUSTOMER);
        editor.remove(BUSINESS_UNIT);
        editor.remove(CREDENTIALS);
        //editor.apply();
        editor.commit();
        mCredentials = null;
    }
}
