package net.ericsson.emovs.exposure.metadata.cache;

import net.ericsson.emovs.exposure.utils.MonotonicTimeService;
import net.ericsson.emovs.utilities.models.EmpProgram;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2018-03-28.
 */

public class EPGCache {
    public int REFRESH_TIME_MS = 15 * 60 * 1000;
    public int REFRESH_TIME_MS_IF_NO_EPG = 60 * 1000;
    String channelId;
    ArrayList<EmpProgram> cache;
    long lastRefresh;

    public EPGCache() {
        this.cache = new ArrayList<>();
    }

    public ArrayList<EmpProgram> getByTime(long time) {
        ArrayList<EmpProgram> result = new ArrayList<>();
        for(EmpProgram p : cache) {
            if (time >= p.startDateTime.getMillis() && time <= p.endDateTime.getMillis()) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean shouldRefresh(String channelId, long epgTimeNowMs) {
        if (this.channelId != channelId) {
            return true;
        }
        if (this.cache != null && this.cache.size() > 0 && epgTimeNowMs > this.cache.get(this.cache.size() - 1).endDateTime.getMillis()) {
            return true;
        }
        ArrayList<EmpProgram> programs = getByTime(epgTimeNowMs);
        if (programs == null || programs.size() == 0) {
            return lastRefresh < MonotonicTimeService.getInstance().currentTime() - REFRESH_TIME_MS_IF_NO_EPG;
        }
        else {
            return lastRefresh < MonotonicTimeService.getInstance().currentTime() - REFRESH_TIME_MS;
        }
    }

    public void update(String channelId, ArrayList<EmpProgram> newCache) {
        this.cache = newCache;
        this.channelId = channelId;
        this.lastRefresh = MonotonicTimeService.getInstance().currentTime();
    }

}
