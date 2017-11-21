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
import android.util.Log;

import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.exposure.interfaces.IAuthenticationListener;
import net.ericsson.emovs.exposure.interfaces.ICredentialsStorageProvider;
import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.emp.EMPRegistry;
import net.ericsson.emovs.utilities.errors.Error;

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

    public static EMPAuthProvider getInstance() {
        EmpAuthProviderHolder.sInstance.setApplicationContext(EMPRegistry.applicationContext());
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
        ExposureClient exposureClient = ExposureClient.getInstance();
        exposureClient.setExposureUrl(storageProvider.getExposureUrl());
        exposureClient.setCustomer(storageProvider.getCustomer());
        exposureClient.setBusinessUnit(storageProvider.getBusinessUnit());
        if (mCredentials != null) {
            exposureClient.setSessionToken(mCredentials.getSessionToken());
        }
    }

    public Boolean isAuthenticated() {
        if(mCredentials == null) {
            mCredentials = SharedPropertiesICredentialsStorage.getInstance().getCredentials();
        }
        return null != mCredentials;
    }

    private void parseAuthResponse(final IAuthenticationListener listener,
                                     final JSONObject response,
                                     final Error error) {
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
                listener.onAuthError(Error.INVALID_JSON);
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
                public void onCallCompleted(JSONObject response, Error error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "anonymous authentication error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
            }
        }
    }

    public void login(String username, String password, final IAuthenticationListener listener) {
        login(true, username, password, null, listener);
    }

    public void login(final Boolean persistent, final String username, final String password, final String mfaCode, final IAuthenticationListener listener) {
        boolean is2factor = mfaCode != null && !mfaCode.equals("");
        String path = "auth/login";
        if (is2factor) {
            path = "/auth/twofactorlogin";
        }
        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", DeviceInfo.getInstance(mApplicationContext).getDeviceId())
                    .put("device", DeviceInfo.getInstance(mApplicationContext).getDeviceInfo())
                    .put("rememberMe", persistent)
                    .put("username", username)
                    .put("password", password);
            if(is2factor) {
                authRequest.put("totp", mfaCode);
            }

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, Error error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
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
                public void onCallCompleted(JSONObject response, Error error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
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
                public void onCallCompleted(JSONObject response, Error error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
            }
        }
    }

    public void checkPassword(final String password, final IAuthenticationListener listener) {
        final String path = "auth/credentials";

        try {
            JSONObject authRequest = new JSONObject().put("password", password);

            ExposureClient.getInstance().postAsync(path, authRequest, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, Error error) {
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
                                listener.onAuthError(Error.INCORRECT_CREDENTIALS);
                            }
                        }
                    } catch (JSONException e0) {
                        Log.e(TAG, "Password check error", e0);
                        if(null != listener) {
                            listener.onAuthError(Error.INVALID_JSON);
                        }
                    }
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Password check error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
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
                public void onCallCompleted(JSONObject response, Error error) {
                    parseAuthResponse(listener, response, error);
                }
            });
        } catch (JSONException e0) {
            Log.e(TAG, "Authentication error", e0);
            if(null != listener) {
                listener.onAuthError(Error.INVALID_JSON);
            }
        }
    }

    public void checkAuth(final IAuthenticationListener listener) {
        final String path = "auth/session";
        if (isAuthenticated()){
            ExposureClient exposureClient = ExposureClient.getInstance();
            exposureClient.setSessionToken(mCredentials.getSessionToken());

            ExposureClient.getInstance().getAsync(path, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, Error error) {
                    if (error != null) {
                        if (listener != null) {
                            listener.onAuthError(error);
                        }
                        return;
                    }
                    listener.onAuthSuccess(mCredentials.getSessionToken());
                }
            });
        } else {
            if (null != listener) {
                listener.onAuthError(Error.NO_SESSION_TOKEN);
            }
        }
    }

    public void logout() {
        Log.d(TAG, "Request logout");
        final String path = "auth/session";

        if (isAuthenticated()){
            ExposureClient.getInstance().deleteAsync(path, new IExposureCallback() {
                @Override
                public void onCallCompleted(JSONObject response, Error error) {
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
