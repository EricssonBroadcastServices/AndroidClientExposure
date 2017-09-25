package com.ebs.android.exposure.auth;

import android.content.Context;

import com.ebs.android.exposure.interfaces.IAuthenticationListener;

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

    public static EMPAuthProvider getInstance(Context context) {
        EMPAuthProviderWithStorage.EMPAuthProviderWithStorageHolder.sInstance.setApplicationContext(context.getApplicationContext());
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
        super.checkAuth(listener);
    }


    private SharedPropertiesICredentialsStorage storage() {
        return SharedPropertiesICredentialsStorage.getInstance(mApplicationContext);
    }
}
