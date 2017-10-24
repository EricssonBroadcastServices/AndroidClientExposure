package com.ebs.android.exposure.auth;

import android.content.Context;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IAuthenticationListener;
import com.ebs.android.exposure.interfaces.ICredentialsStorageProvider;

import net.ericsson.emovs.utilities.ContextRegistry;

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

    public static EMPAuthProvider getInstance(String apiUrl, String customer, String businessUnit) {
        SharedPropertiesICredentialsStorage sharedStorage = SharedPropertiesICredentialsStorage.getInstance();
        sharedStorage.storeCredentials(apiUrl, customer, businessUnit, null);
        EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance.setApplicationContext(ContextRegistry.get());
        EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance.setCredentialsStore(sharedStorage);
        return EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance;
    }

    @Override
    public Boolean isAuthenticated() {
        if(mCredentials == null) {
            mCredentials = SharedPropertiesICredentialsStorage.getInstance().getCredentials();
        }
        return null != mCredentials;
    }

    @Override
    public void checkAuth(final IAuthenticationListener listener) {
        if(mCredentials == null) {
            mCredentials = SharedPropertiesICredentialsStorage.getInstance().getCredentials();
        }
        super.checkAuth(new IAuthenticationListener() {
            @Override
            public void onAuthSuccess(String sessionToken) {
                listener.onAuthSuccess(sessionToken);
            }

            @Override
            public void onAuthError(ExposureError error) {
                SharedPropertiesICredentialsStorage.getInstance().deleteCredentials();
                listener.onAuthError(error);
            }
        });
    }
}
