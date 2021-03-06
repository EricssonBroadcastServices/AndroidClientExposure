package net.ericsson.emovs.exposure.metadata;

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

import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.exposure.metadata.builders.AssetListBuilder;
import net.ericsson.emovs.exposure.metadata.builders.AutocompleteBuilder;
import net.ericsson.emovs.exposure.metadata.builders.CarouselGroupBuilder;
import net.ericsson.emovs.exposure.metadata.builders.ChannelsBuilder;
import net.ericsson.emovs.exposure.metadata.builders.EmpProgramBuilder;
import net.ericsson.emovs.exposure.metadata.builders.EpgBuilder;
import net.ericsson.emovs.exposure.metadata.builders.MainConfigBuilder;
import net.ericsson.emovs.exposure.metadata.builders.SeriesBuilder;
import net.ericsson.emovs.exposure.metadata.cache.EPGCache;
import net.ericsson.emovs.utilities.interfaces.IMetadataCallback;
import net.ericsson.emovs.utilities.interfaces.IMetadataProvider;
import net.ericsson.emovs.utilities.queries.ChannelsQueryParameters;
import net.ericsson.emovs.utilities.queries.EpgQueryParameters;
import net.ericsson.emovs.utilities.queries.SeriesQueryParameters;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.models.EmpCarousel;
import net.ericsson.emovs.utilities.models.EmpChannel;
import net.ericsson.emovs.utilities.models.EmpCustomer;
import net.ericsson.emovs.utilities.models.EmpProgram;
import net.ericsson.emovs.utilities.models.EmpSeries;
import net.ericsson.emovs.utilities.errors.Error;

import java.util.ArrayList;

public class EMPMetadataProvider implements IMetadataProvider {
    private static final String TAG = "EMPMetadataProvider";
    public EPGCache epgCache;

    private static class EMPMediaMetadataProviderHolder {
        private final static EMPMetadataProvider sInstance = new EMPMetadataProvider();
    }

    public static EMPMetadataProvider getInstance() {
        return EMPMediaMetadataProviderHolder.sInstance;
    }

    protected EMPMetadataProvider() {
        epgCache = new EPGCache();
    }

    public void getSeries(IMetadataCallback<ArrayList<EmpSeries>> callback) {
        getSeries(callback, SeriesQueryParameters.getDefault());
    }

    public void getSeries(final IMetadataCallback<ArrayList<EmpSeries>> callback, SeriesQueryParameters params) {
        makeRequest("/content/season?includeEpisodes=" + params.includeEpisodes(), new SeriesBuilder(callback));
    }

    public void getProgramDetails(String channelId, String programId, IMetadataCallback<EmpProgram> callback) {
        makeRequest("/epg/" + channelId + "/program/" + programId, new EmpProgramBuilder(callback));
    }

    public void getEpgCacheFirst(final String channelId, final long epgTimeNowMs, final IMetadataCallback<ArrayList<EmpProgram>> callback, EpgQueryParameters params) {
        if (params == null) {
            params = EpgQueryParameters.DEFAULT.clone();
        }
        IMetadataCallback cacheListener = new IMetadataCallback<ArrayList<EmpProgram>>() {
            @Override
            public void onMetadata(ArrayList<EmpProgram> metadata) {
                epgCache.update(channelId, metadata);
                if (callback != null) {
                    ArrayList<EmpProgram> newMetadata = epgCache.getByTime(epgTimeNowMs);
                    callback.onMetadata(newMetadata);
                }
            }

            @Override
            public void onError(Error error) {
                if (callback != null) {
                    callback.onError(error);
                }
            }
        };

        params.setPastTimeFrame(60 * 60 * 1000);
        params.setFutureTimeFrame(60 * 60 * 1000);
        long from = epgTimeNowMs - params.getPastTimeFrame();
        long to = epgTimeNowMs + params.getFutureTimeFrame();

        if (epgCache.shouldRefresh(channelId, epgTimeNowMs)) {
            makeRequest("/epg/" + channelId + "?from=" + from + "&to=" + to + "&pageSize=" + params.getPageSize(), new EpgBuilder(cacheListener));
            return;
        }

        if (callback != null) {
            ArrayList<EmpProgram> cachedPrograms = epgCache.getByTime(epgTimeNowMs);
            callback.onMetadata(cachedPrograms);
        }
    }

    public void getEpgWithTime(String channelId, long epgTimeNowMs, IMetadataCallback<ArrayList<EmpProgram>> callback, EpgQueryParameters params) {
        long nowMs = epgTimeNowMs;
        long from = nowMs - params.getPastTimeFrame();
        long to = nowMs + params.getFutureTimeFrame();
        makeRequest("/epg/" + channelId + "?from=" + from + "&to=" + to + "&pageSize=" + params.getPageSize(), new EpgBuilder(callback));
    }

    public void getEpg(String channelId, IMetadataCallback<ArrayList<EmpProgram>> callback, EpgQueryParameters params) {
        long nowMs = System.currentTimeMillis();
        long from = nowMs - params.getPastTimeFrame();
        long to = nowMs + params.getFutureTimeFrame();
        makeRequest("/epg/" + channelId + "?from=" + from + "&to=" + to + "&pageSize=" + params.getPageSize(), new EpgBuilder(callback));
    }

    public void getChannels(IMetadataCallback<ArrayList<EmpChannel>> callback, ChannelsQueryParameters params) {
        String url = "/content/asset?"
                + "fieldSet=" + params.getFieldSet()
                + "&includeUserData=" + params.isIncludeUserData()
                + "&pageNumber=" + params.getPageNumber()
                + "&sort=" + params.getSort()
                + "&pageSize=" + params.getPageSize()
                + "&onlyPublished=" + params.isOnlyPublished()
                + "&assetType=TV_CHANNEL";
        makeRequest(url, new ChannelsBuilder(callback));
    }

    public void getAssets(String endpoint, IMetadataCallback<ArrayList<EmpAsset>> callback) {
        makeRequest(endpoint, new AssetListBuilder(callback));
    }

    public void autocomplete(String query, IMetadataCallback<ArrayList<EmpAsset>> callback) {
        makeRequest("/content/search/autocomplete/" + query, new AutocompleteBuilder(callback));
    }

    public void getCarouselGroupById(String carouselGroupId, IMetadataCallback<ArrayList<EmpCarousel>> callback) {
        makeRequest("/carouselgroup/" + carouselGroupId, new CarouselGroupBuilder(callback));
    }

    public void getMainJson(IMetadataCallback<EmpCustomer> callback) {
        makeRequest("/config/main.json", new MainConfigBuilder(callback), false);
    }

    private void makeRequest(final String path, final IExposureCallback listener) {
        makeRequest(path, listener, false);
    }

    private void makeRequest(final String path, final IExposureCallback listener, boolean requireAuth) {
        ExposureClient exposureClient = ExposureClient.getInstance();
        if (requireAuth && exposureClient.getSessionToken() == null) {
            listener.onCallCompleted(null, Error.NO_SESSION_TOKEN);
            return;
        }
        ExposureClient.getInstance().getAsync(path, listener);
    }

}
