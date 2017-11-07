package com.ebs.android.exposure.metadata;/*
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

import com.ebs.android.exposure.clients.exposure.ExposureClient;
import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.builders.AssetListBuilder;
import com.ebs.android.exposure.metadata.builders.AutocompleteBuilder;
import com.ebs.android.exposure.metadata.builders.CarouselGroupBuilder;
import com.ebs.android.exposure.metadata.builders.ChannelsBuilder;
import com.ebs.android.exposure.metadata.builders.EpgBuilder;
import com.ebs.android.exposure.metadata.builders.MainConfigBuilder;
import com.ebs.android.exposure.metadata.builders.SeriesBuilder;
import com.ebs.android.exposure.metadata.queries.ChannelsQueryParameters;
import com.ebs.android.exposure.metadata.queries.EpgQueryParameters;
import com.ebs.android.exposure.metadata.queries.SeriesQueryParameters;
import com.ebs.android.exposure.models.EmpAsset;
import com.ebs.android.exposure.models.EmpCarousel;
import com.ebs.android.exposure.models.EmpChannel;
import com.ebs.android.exposure.models.EmpCustomer;
import com.ebs.android.exposure.models.EmpProgram;
import com.ebs.android.exposure.models.EmpSeries;

import java.util.ArrayList;

public class EMPMetadataProvider {
    private static final String TAG = "EMPMetadataProvider";

    private static class EMPMediaMetadataProviderHolder {
        private final static EMPMetadataProvider sInstance = new EMPMetadataProvider();
    }

    public static EMPMetadataProvider getInstance() {
        return EMPMediaMetadataProviderHolder.sInstance;
    }

    protected EMPMetadataProvider() {
    }

    public void getSeries(IMetadataCallback<ArrayList<EmpSeries>> callback) {
        getSeries(callback, SeriesQueryParameters.getDefault());
    }

    public void getSeries(final IMetadataCallback<ArrayList<EmpSeries>> callback, SeriesQueryParameters params) {
        makeRequest("/content/season?includeEpisodes=" + params.includeEpisodes(), new SeriesBuilder(callback));
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
        makeRequest("/config/main.json", new MainConfigBuilder(callback));
    }

    private void makeRequest(final String path, final IExposureCallback listener) {
        ExposureClient exposureClient = ExposureClient.getInstance();
        if (exposureClient.getSessionToken() == null) {
            listener.onCallCompleted(null, ExposureError.NO_SESSION_TOKEN);
            return;
        }
        ExposureClient.getInstance().getAsync(path, listener);
    }

}
