package com.ebs.android.exposure.entitlements;

import android.util.Log;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IEntitlementCallback;
import com.ebs.android.utilities.ErrorCodes;
import com.ebs.android.utilities.ErrorRunnable;
import com.ebs.android.utilities.RunnableThread;

/**
 * Created by Joao Coelho on 2017-09-26.
 */

public class EntitlementCallback implements IEntitlementCallback {
    String assetId;
    String channelId;
    String programId;
    EntitledRunnable runnable;
    ErrorRunnable errorRunnable;

    public EntitlementCallback(String assetId, String channelId, String programId, EntitledRunnable runnable, ErrorRunnable errorRunnable) {
        this.assetId = assetId;
        this.channelId = channelId;
        this.programId = programId;
        this.runnable = runnable;
        this.errorRunnable = errorRunnable;
    }

    @Override
    public void onEntitlement(final Entitlement entitlement) {
        this.runnable.entitlement = entitlement;
        new RunnableThread(this.runnable).start();
    }

    @Override
    public void onError(ExposureError error) {
        Log.e("EXO PLAYER ERROR", error.toString());
        if (errorRunnable != null) {
            errorRunnable.run(ErrorCodes.EXPOSURE_ENTITLEMENT_ERROR, error.toString());
        }
    }

    @Override
    public String getAssetId() {
        return this.assetId;
    }

    @Override
    public String getChannelId() {
        return this.channelId;
    }

    @Override
    public String getProgramId() {
        return this.programId;
    }
}
