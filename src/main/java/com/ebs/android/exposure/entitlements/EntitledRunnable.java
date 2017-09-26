package com.ebs.android.exposure.entitlements;

/**
 * Created by Joao Coelho on 2017-09-26.
 */

public class EntitledRunnable implements Runnable {
    public Entitlement entitlement;
    @Override
    public void run() {
        throw new RuntimeException("Stub!");
    }
}
