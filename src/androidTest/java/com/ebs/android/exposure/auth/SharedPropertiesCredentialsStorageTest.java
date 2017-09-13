package com.ebs.android.exposure.auth;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

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
@RunWith(AndroidJUnit4.class)
public class SharedPropertiesCredentialsStorageTest {
    private final String API_URL = "https://exposure.emps.ebsd.ericsson.net/";
    final String CUSTOMER = "BlixtGroup";
    final String BUSINESS_UNIT = "Blixt";
    private final String AUTH_RESPONSE = "{\n" +
            "  \"sessionToken\" : \"A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp|151103182419922|14873|f3a526e0a4ba8010f67ee37224bcc5ab|1487714577459|1487800977000|false|WEB_Thijs987|1n8HfeZvvIWI3b5kneiiDucElMC+lNZVlKCXz9AEon4=\",\n" +
            "  \"crmToken\" : \"A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp\",\n" +
            "  \"accountId\" : \"151103182419922\",\n" +
            "  \"expirationDateTime\" : \"2017-02-22T22:02:57Z\",\n" +
            "  \"accountStatus\" : \"Active\"\n" +
            "}";

    @Test
    public void testStoreCredentials() throws Exception {
         Context appContext = InstrumentationRegistry.getTargetContext();

        SharedPropertiesCredentialsStorage sharedPropertiesCredentialsStorage = SharedPropertiesCredentialsStorage.getInstance(appContext);
        sharedPropertiesCredentialsStorage.storeCredentials(API_URL, CUSTOMER, BUSINESS_UNIT, Credentials.fromJSON(new JSONObject(AUTH_RESPONSE)));

        assertEquals(API_URL, sharedPropertiesCredentialsStorage.getExposureUrl());
        assertEquals(CUSTOMER, sharedPropertiesCredentialsStorage.getCustomer());
        assertEquals(BUSINESS_UNIT, sharedPropertiesCredentialsStorage.getBusinessUnit());
        assertNotNull(sharedPropertiesCredentialsStorage.getCredentials());
    }

    @Test
    public void testDeleteCredentials() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        SharedPropertiesCredentialsStorage sharedPropertiesCredentialsStorage = SharedPropertiesCredentialsStorage.getInstance(appContext);
        sharedPropertiesCredentialsStorage.storeCredentials(API_URL, CUSTOMER, BUSINESS_UNIT, Credentials.fromJSON(new JSONObject(AUTH_RESPONSE)));

        sharedPropertiesCredentialsStorage.deleteCredentials();

        assertEquals("", sharedPropertiesCredentialsStorage.getExposureUrl());
        assertEquals("", sharedPropertiesCredentialsStorage.getCustomer());
        assertEquals("", sharedPropertiesCredentialsStorage.getBusinessUnit());
        assertNull(sharedPropertiesCredentialsStorage.getCredentials());
    }

}