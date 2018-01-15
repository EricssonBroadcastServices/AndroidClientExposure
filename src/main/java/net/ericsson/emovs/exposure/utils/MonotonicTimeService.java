package net.ericsson.emovs.exposure.utils;

import android.content.Context;

import net.ericsson.emovs.exposure.auth.EMPAuthProvider;
import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.exposure.entitlements.EMPEntitlementProvider;
import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.emp.EMPRegistry;
import net.ericsson.emovs.utilities.errors.Error;

import org.joda.time.DateTime;
import org.json.JSONObject;

/**
 * Created by Joao Coelho on 2018-01-03.
 */

public class MonotonicTimeService extends Thread {
    long REFRESH_INTERVAL = 60000 * 30;
    long EXPOSURE_DOWN_INTERVAL = 1000;

    long serverStartTime;
    long localStartTime;
    boolean running;

    private static class MonotonicTimeServiceHolder {
        private final static MonotonicTimeService sInstance = new MonotonicTimeService();
    }

    public static MonotonicTimeService getInstance() {
        if (!MonotonicTimeServiceHolder.sInstance.isAlive()) {
            MonotonicTimeServiceHolder.sInstance.start();
        }
        return MonotonicTimeServiceHolder.sInstance;
    }

    public static void destroyInstance() {
        if (MonotonicTimeServiceHolder.sInstance.isAlive() && !MonotonicTimeServiceHolder.sInstance.isInterrupted()) {
            MonotonicTimeServiceHolder.sInstance.interrupt();
        }
    }

    public MonotonicTimeService() {

    }

    public void run() {
        this.running = false;
        while (true) {
            try {
                ExposureClient.getInstance().getSync("/time", new IExposureCallback() {
                    @Override
                    public void onCallCompleted(JSONObject response, Error error) {
                        if (error != null) {
                            return;
                        }
                        if (response.has("epochMillis") == false) {
                            return;
                        }
                        serverStartTime = response.optLong("epochMillis");
                        localStartTime = System.currentTimeMillis();
                        running = true;
                    }
                });
                if (running == false) {
                    Thread.sleep(EXPOSURE_DOWN_INTERVAL);
                } else {
                    Thread.sleep(REFRESH_INTERVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.running = false;
            }
        }
    }

    public Long currentTime() {
        try {
            int n = 0;
            while (!this.running && n < 5000) {
                Thread.sleep(1);
                ++n;
            }
            if(!this.running) {
                return null;
            }
            if(this.running && serverStartTime > 0) {
                return this.serverStartTime + (System.currentTimeMillis() - this.localStartTime);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
