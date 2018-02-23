package com.ebs.android.exposure.exposure;

import net.ericsson.emovs.exposure.utils.MonotonicTimeService;
import net.ericsson.emovs.utilities.emp.EMPRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Joao Coelho on 2018-01-04.
 */

@RunWith(RobolectricTestRunner.class)
public class MonotonicTimeTest {
    public static String API_URL = "https://psempexposureapi.ebsd.ericsson.net";
    public static String CUSTOMER = "DevGroup";
    public static String BUSSINESS_UNIT = "EnigmaTV";

    @Test
    @Config(manifest=Config.NONE)
    public void testTime1() throws Exception {
        EMPRegistry.bindExposureContext(API_URL, CUSTOMER, BUSSINESS_UNIT);
        MonotonicTimeService timeService = new MonotonicTimeService();
        timeService.start();

        long t1 = timeService.currentTime();

        Thread.sleep(5000);

        long t2 = timeService.currentTime();

        Assert.assertTrue(t2 - t1 > 4500 && t2 - t1 < 5500);
    }

}
