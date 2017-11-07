package net.ericsson.emovs.exposure.auth;

import net.ericsson.emovs.exposure.auth.Credentials;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

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
//@RunWith(AndroidJUnit4.class)
public class CredentialsTest {
    private final String AUTH_RESPONSE = "{\"sessionToken\":\"CD1X-tiIQ-x7vI-Zcsy-ZHNJ-olBO-6E|160304085954428|48000|cec24d6511aea8b5c3c6a839d4f1d0a4|1507128381911|1507153581000|false|WEB_blixtuser1|Alzhu4LhTd8XoFW6Iz8qUpFHtYUb9UtkPOQ9u7Npclw=\",\"crmToken\":\"CD1X-tiIQ-x7vI-Zcsy-ZHNJ-olBO-6E\",\"accountId\":\"160304085954428\",\"expirationDateTime\":\"2017-10-04T21:46:21.00Z\",\"accountStatus\":\"Active\"}";

    private Credentials testCredentials;

    @Before
    public void setUp() throws Exception {
        testCredentials = Credentials.fromJSON(new JSONObject(AUTH_RESPONSE));
    }

    @Test
    public void getSessionToken() throws Exception {
        assertEquals("CD1X-tiIQ-x7vI-Zcsy-ZHNJ-olBO-6E|160304085954428|48000|cec24d6511aea8b5c3c6a839d4f1d0a4|1507128381911|1507153581000|false|WEB_blixtuser1|Alzhu4LhTd8XoFW6Iz8qUpFHtYUb9UtkPOQ9u7Npclw=", testCredentials.getSessionToken());
    }

    @Test
    public void getCrmToken() throws Exception {
        assertEquals("CD1X-tiIQ-x7vI-Zcsy-ZHNJ-olBO-6E", testCredentials.getCrmToken());
    }

    @Test
    public void getAccountId() throws Exception {
        assertEquals("160304085954428", testCredentials.getAccountId());
    }

    @Test
    public void getExpiration() throws Exception {
        Calendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        gc.set(2017, Calendar.OCTOBER, 04, 21, 46, 21);
        gc.set(Calendar.MILLISECOND, 0);
        assertEquals(gc.getTime(), testCredentials.getExpiration());
    }

    @Test
    public void getAccountStatus() throws Exception {
        assertEquals("Active", testCredentials.getAccountStatus());
    }

}