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

import net.ericsson.emovs.utilities.DateTimeParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Credentials {
    public final String mSessionToken;
    public final String mCrmToken;
    public final String mAccountId;
    public final Date mExpiration;
    public final String mAccountStatus;

    private Credentials(String sessionToken, String crmToken, String accountId, Date expiration,
                        String accountStatus) {
        mSessionToken = sessionToken;
        mCrmToken = crmToken;
        mAccountId = accountId;
        mExpiration = expiration;
        mAccountStatus = accountStatus;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public String getCrmToken() {
        return mCrmToken;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public String getAccountStatus() {
        return mAccountStatus;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject credentials = new JSONObject();

        credentials.put("sessionToken", mSessionToken);
        credentials.put("crmToken", mCrmToken);
        credentials.put("accountId", mAccountId);
        credentials.put("expirationDateTime", DateTimeParser.formatUtcDateTime(mExpiration));
        credentials.put("accountStatus", mAccountStatus);

        return credentials;
    }

    public static Credentials fromJSON(JSONObject credentials) throws JSONException, ParseException {
        String crmToken = "";
        String accountId = "";
        Date expirationDateTime = new Date(Long.MAX_VALUE);
        String accountStatus = "";

        String sessionToken = credentials.getString("sessionToken");
        if (credentials.has("crmToken")) {
            crmToken = credentials.getString("crmToken");
        }
        if (credentials.has("accountId")) {
            accountId = credentials.getString("accountId");
        }
        if (credentials.has("expirationDateTime")) {
            expirationDateTime = DateTimeParser.parseUtcDateTime(credentials.getString("expirationDateTime"));
        }
        if (credentials.has("accountStatus")) {
            accountStatus = credentials.getString("accountStatus");
        }
        return new Credentials(sessionToken, crmToken, accountId, expirationDateTime, accountStatus);
    }

    @Override
    public String toString() {
        try {
            return toJSON().toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
