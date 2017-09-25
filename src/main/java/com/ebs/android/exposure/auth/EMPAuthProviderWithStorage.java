package com.ebs.android.exposure.auth;

import android.content.Context;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IAuthenticationListener;
import com.ebs.android.exposure.interfaces.ICredentialsStorageProvider;

/**
 * Created by Joao Coelho on 2017-09-25.
 */

public class EMPAuthProviderWithStorage extends EMPAuthProvider {

    private EMPAuthProviderWithStorage() {
        super();
    }

    private static class EMPAuthProviderWithStorageHolder {
        private final static EMPAuthProviderWithStorage sInstance = new EMPAuthProviderWithStorage();
    }

    public static EMPAuthProvider getInstance(Context context, String apiUrl, String customer, String businessUnit) {
        SharedPropertiesICredentialsStorage sharedStorage = storage(context);
        sharedStorage.storeCredentials(apiUrl, customer, businessUnit, null);
        EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance.setApplicationContext(context);
        EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance.setCredentialsStore(sharedStorage);
        return EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance;
    }

    @Override
    public Boolean isAuthenticated() {
        if(mCredentials == null) {
            mCredentials = storage().getCredentials();
        }
        return null != mCredentials;
    }

    @Override
    public void checkAuth(final IAuthenticationListener listener) {
        if(mCredentials == null) {
            mCredentials = storage().getCredentials();
        }
        super.checkAuth(new IAuthenticationListener() {
            @Override
            public void onAuthSuccess(String sessionToken) {
                listener.onAuthSuccess(sessionToken);
            }

            @Override
            public void onAuthError(ExposureError error) {
                storage().deleteCredentials();
                listener.onAuthError(error);
            }
        });
    }

    private SharedPropertiesICredentialsStorage storage() {
        return SharedPropertiesICredentialsStorage.getInstance(mApplicationContext);
    }

    private static SharedPropertiesICredentialsStorage storage(Context context) {
        return SharedPropertiesICredentialsStorage.getInstance(context);
    }
}
