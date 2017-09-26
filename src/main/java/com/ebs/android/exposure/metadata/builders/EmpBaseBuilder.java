package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpAsset;
import com.ebs.android.exposure.models.EmpProgram;

import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joao Coelho on 2017-07-19.
 */

public class EmpBaseBuilder {
    protected IMetadataCallback listener;

    public EmpBaseBuilder(IMetadataCallback listener) {
        this.listener = listener;
    }

    protected boolean handleError(ExposureError error) {
        if (listener == null) {
            return true;
        }

        if (error != null) {
            this.listener.onError(error);
            return true;
        }

        return false;
    }

    protected String getLocalized(JSONObject obj, String locale, String property) throws JSONException {
        JSONArray localized = obj.getJSONArray("localized");
        String propertyToReturn = "";
        for(int i = 0; i < localized.length(); ++i) {
            JSONObject localeData = localized.getJSONObject(i);
            String propLocale = localeData.getString("locale");
            if(i == 0) {
                propertyToReturn = localeData.getString(property);
            }

            if(locale.equals(propLocale)) {
                propertyToReturn = localeData.getString(property);
            }
        }

        return propertyToReturn;
    }

    protected String getLocalized(JSONArray localized, String locale, String property) throws JSONException {
        String propertyToReturn = "";
        for(int i = 0; i < localized.length(); ++i) {
            JSONObject localeData = localized.getJSONObject(i);
            String propLocale = localeData.getString("locale");
            if(i == 0) {
                propertyToReturn = localeData.getString(property);
            }

            if(locale.equals(propLocale)) {
                propertyToReturn = localeData.getString(property);
            }
        }

        return propertyToReturn;
    }

    protected String getLocalizedImages(JSONObject obj, String locale, String imgType) throws JSONException {
        JSONArray localized = obj.getJSONArray("localized");
        JSONArray images = null;
        String logoUrl = null;
        for(int i = 0; i < localized.length(); ++i) {
            JSONObject localeData = localized.getJSONObject(i);
            String propLocale = localeData.getString("locale");
            if(i == 0) {
                images = localeData.getJSONArray("images");
            }

            if(locale.equals(propLocale)) {
                images = localeData.getJSONArray("images");
            }
        }

        if(images != null) {
            for(int i = 0; i < images.length(); ++i) {
                JSONObject imageData = images.getJSONObject(i);
                String imgTypeData = imageData.getString("type");
                if(i == 0) {
                    logoUrl = imageData.getString("url");
                }

                if(imgType.equals(imgTypeData)) {
                    logoUrl = imageData.getString("url");
                }
            }

        }

        return logoUrl;
    }

    protected EmpAsset getAsset(JSONObject assetJson, EmpAsset asset, boolean checkEmptyMedias) throws JSONException {
        asset.title = getLocalized(assetJson, "en", "title");
        asset.imageUrl = getLocalizedImages(assetJson, "en", "banner");
        asset.assetId = assetJson.getString("assetId");
        asset.resolution = "";
        asset.popularity = "";
        asset.duration = "";
        if(checkEmptyMedias) {
            JSONArray medias = assetJson.getJSONArray("medias");
            if(medias.length() == 0) {
                asset = null;
            }
        }

        return asset;
    }

    protected EmpProgram getProgram(JSONObject programJson, EmpProgram program) throws JSONException {
        String startTimeStr = programJson.getString("startTime");
        String endTimeStr = programJson.getString("endTime");
        program.startDateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(startTimeStr);
        program.endDateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(endTimeStr);
        program.programId = programJson.getString("programId");
        return program;
    }
}
