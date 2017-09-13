package com.ebs.android.exposure.auth;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
public class CredentialsTest {
    private final String AUTH_RESPONSE = "{\n" +
            "  \"sessionToken\" : \"A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp|151103182419922|14873|f3a526e0a4ba8010f67ee37224bcc5ab|1487714577459|1487800977000|false|WEB_Thijs987|1n8HfeZvvIWI3b5kneiiDucElMC+lNZVlKCXz9AEon4=\",\n" +
            "  \"crmToken\" : \"A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp\",\n" +
            "  \"accountId\" : \"151103182419922\",\n" +
            "  \"expirationDateTime\" : \"2017-02-22T22:02:57Z\",\n" +
            "  \"accountStatus\" : \"Active\"\n" +
            "}";

    private Credentials testCredentials;

    @Before
    public void setUp() throws Exception {
        testCredentials = Credentials.fromJSON(new JSONObject(AUTH_RESPONSE));
    }

    @Test
    public void getSessionToken() throws Exception {
        assertEquals("A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp|151103182419922|14873|f3a526e0a4ba8010f67ee37224bcc5ab|1487714577459|1487800977000|false|WEB_Thijs987|1n8HfeZvvIWI3b5kneiiDucElMC+lNZVlKCXz9AEon4=", testCredentials.getSessionToken());
    }

    @Test
    public void getCrmToken() throws Exception {
        assertEquals("A29i-4mTJ-AwOK-4yeu-kryc-MGaa-rp", testCredentials.getCrmToken());
    }

    @Test
    public void getAccountId() throws Exception {
        assertEquals("151103182419922", testCredentials.getAccountId());
    }

    @Test
    public void getExpiration() throws Exception {
        Calendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        gc.set(2017, 1, 22, 22, 2, 57);
        gc.set(Calendar.MILLISECOND, 0);
        assertEquals(gc.getTime(), testCredentials.getExpiration());
    }

    @Test
    public void getAccountStatus() throws Exception {
        assertEquals("Active", testCredentials.getAccountStatus());
    }

}