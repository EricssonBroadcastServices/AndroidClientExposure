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
import android.util.Log;

import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.clients.exposure.ExposureClient;
import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IAuthenticationListener;
import com.ebs.android.exposure.interfaces.ICredentialsStorageProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class EMPAuthProvider {
    private static final String TAG = "EMPAuthProvider";

    protected Context mApplicationContext;
    private ICredentialsStorageProvider mICredentialsStorageProvider;

    protected Credentials mCredentials = null;

    private static class EmpAuthProviderHolder {
        private final static EMPAuthProvider sInstance = new EMPAuthProvider();
    }

    public static EMPAuthProvider getInstance(Context context) {
        EmpAuthProviderHolder.sInstance.setApplicationContext(context.getApplicationContext());
        return EmpAuthProviderHolder.sInstance;
    }

    protected EMPAuthProvider() {
    }

    protected void setApplicationContext(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public void setCredentialsStore(ICredentialsStorageProvider storageProvider) {
        mICredentialsStorageProvider = storageProvider;
        mCredentials = storageProvider.getCredentials();

        if (null != mCredentials) {
            ExposureClient exposureClient = ExposureClient.getInstance();
            exposureClient.setExposureUrl(storageProvider.getExposureUrl());
            exposureClient.setCustomer(storageProvider.getCustomer());
            exposureClient.setBusinessUnit(storageProvider.getBusinessUnit());
            exposureClient.setSessionToken(mCredentials.getSessionToken());
        }
    }

    public Boolean isAuthenticated() {
        return null != mCredentials;
    }

    private void parseAuthResponse(final IAuthenticationListener listener,
                                     final JSONObject response,
                                     final ExposureError error) {
        if (null != error) {
            if (null != listener) {
                listener.onAuthError(error);
            }
            return;
        }

        try {
            mCredentials = Credentials.fromJSON(response);

            if (null != mICredentialsStorageProvider) {
                ExposureClient exposureClient = ExposureClient.getInstance();
                exposureClient.setSessionToken(mCredentials.getSessionToken());
                mICredentialsStorageProvider.storeCredentials(exposureClient.getExposureUrl(),
                        exposureClient.getCustomer(), exposureClient.getBusinessUnit(), mCredentials);
            }

            if (null != listener) {
                listener.onAuthSuccess(mCredentials.getSessionToken());
            }

        } catch (JSONException | ParseException ex) {
            Log.e(TAG, "Error parsing exposure response", ex);
            if (null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void anonymous(final IAuthenticationListener listener) {
        final String path = "auth/anonymous";

        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo());

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "anonymous authentication error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void login(String username, String password, final IAuthenticationListener listener) {
        login(true, username, password, listener);
    }

    public void login(final Boolean persistent, final String username, final String password, final IAuthenticationListener listener) {
        final String path = "auth/login";

        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo())
                    .put("rememberMe", persistent)
                    .put("username", username)
                    .put("password", password);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void oAuthLogin(final String accessToken, final String type, final IAuthenticationListener listener) {
        oAuthLogin(true, accessToken, type, listener);
    }

    public void oAuthLogin(final Boolean persistent, final String accessToken, final String type, final IAuthenticationListener listener) {
        final String path = "auth/oauthLogin";

        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo())
                    .put("rememberMe", persistent)
                    .put("accessToken", accessToken)
                    .put("type", type);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void facebook(final String accessToken, final IAuthenticationListener listener) {
        facebook(true, accessToken, listener);
    }

    public void facebook(final Boolean persistent, final String accessToken, final IAuthenticationListener listener) {
        final String path = "auth/facebookLogin";

        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo())
                    .put("rememberMe", persistent)
                    .put("accessToken", accessToken);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void checkPassword(final String password, final IAuthenticationListener listener) {
        final String path = "auth/credentials";

        try {
            JSONObject authRequest = new JSONObject().put("password", password);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    if (null != error) {
                        if (null != listener) {
                            listener.onAuthError(error);
                        }
                        return;
                    }

                    try {
                        if (response.getBoolean("valid")) {
                            if (null != listener) {
                                listener.onAuthSuccess(null);
                            }
                        } else {
                            if (null != listener) {
                                listener.onAuthError(ExposureError.INCORRECT_CREDENTIALS);
                            }
                        }
                    } catch (JSONException e0) {
                        Log.e(TAG, "Password check error", e0);
                        if(null != listener) {
                            listener.onAuthError(ExposureError.INVALID_JSON);
                        }
                    }
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Password check error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void crmSession(final String crmToken, final IAuthenticationListener listener) {
        final String path = "auth/crmSession";

        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo())
                    .put("crmToken", crmToken);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(ExposureError.INVALID_JSON);
            }
        }
    }

    public void checkAuth(final IAuthenticationListener listener) {
        final String path = "auth/session";
        if (isAuthenticated()){
            listener.onAuthSuccess(null);
        } else {
            if (null != listener) {
                listener.onAuthError(ExposureError.NO_SESSION_TOKEN);
            }
        }
    }

    public void logout() {
        Log.d(TAG, "Request logout");
        final String path = "auth/session";

        if (isAuthenticated()){
            ExposureClient.getInstance().deleteAsync(path, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, ExposureError error) {
                    ExposureClient.getInstance().setSessionToken("");
                    if (null != error) {
                        Log.e(TAG, "logout error:" + error.toString());
                    } else {
                        Log.d(TAG, "logout successful");
                    }
                }
            });

            mCredentials = null;
            if (null != mICredentialsStorageProvider) {
                mICredentialsStorageProvider.deleteCredentials();
            }
        }
    }
}
