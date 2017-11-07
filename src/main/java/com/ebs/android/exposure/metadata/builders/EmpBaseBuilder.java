package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpAsset;
import com.ebs.android.exposure.models.EmpImage;
import com.ebs.android.exposure.models.EmpProgram;
import com.ebs.android.exposure.models.LocalizedMetadata;

import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
            if(i == 0 && localeData.has(property)) {
                propertyToReturn = localeData.getString(property);
            }

            if(locale.equals(propLocale) && localeData.has(property)) {
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
                propertyToReturn = localeData.optString(property, "");
            }

            if(locale.equals(propLocale)) {
                propertyToReturn = localeData.optString(property, "");
            }
        }

        return propertyToReturn;
    }

    protected void fillLocalized(JSONObject obj, LocalizedMetadata metadata) throws JSONException {
        JSONArray localized = obj.has("localized") ? obj.getJSONArray("localized") : obj.optJSONArray("titles");
        if (localized == null) {
            return;
        }
        for (int i = 0; i < localized.length(); ++i) {
            JSONObject localeData = localized.getJSONObject(i);
            String propLocale = localeData.optString("locale", "");
            JSONArray images = localeData.optJSONArray("images");
            ArrayList<EmpImage> empImages = new ArrayList<>();
            for (int j = 0; images != null && j < images.length(); ++j) {
                JSONObject image = images.getJSONObject(j);
                EmpImage empImg = imageFromJson(image);
                empImages.add(empImg);
            }
            metadata.images.put(propLocale, empImages);
            metadata.titles.put(propLocale, localeData.optString("title", null));
            metadata.descriptions.put(propLocale, localeData.optString("description", null));
        }
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
                if (imageData.has("type") == false) {
                    continue;
                }
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

    public EmpAsset getAsset(JSONObject assetJson, EmpAsset asset, boolean checkEmptyMedias) throws JSONException {
        asset.originalTitle = assetJson.optString("originalTitle", null);
        asset.assetId = assetJson.getString("assetId");
        if (assetJson.has("userData")) {
            if (assetJson.getJSONObject("userData").has("playHistory")) {
                JSONObject playHistoryJson = assetJson.getJSONObject("userData").getJSONObject("playHistory");
                asset.lastViewedOffset = playHistoryJson.optLong("lastViewedOffset");
                asset.lastViewedTime = playHistoryJson.optLong("lastViewedTime");
            }
        }
        asset.setJson(assetJson);
        if (checkEmptyMedias) {
            JSONArray medias = assetJson.getJSONArray("medias");
            if (medias.length() == 0) {
                asset = null;
                return asset;
            }
            for (int i = 0; i < medias.length(); ++i) {
                JSONObject mediaJson = medias.getJSONObject(i);
                asset.durationMillis = mediaJson.optLong("durationMillis");
                break;
            }
        }
        fillLocalized(assetJson, asset.localized);
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

    public static EmpImage imageFromJson(JSONObject imageJson) {
        EmpImage image = new EmpImage();
        image.url = imageJson.optString("url", null);
        image.width = imageJson.optInt("width", 0);
        image.height = imageJson.optInt("height", 0);
        image.type = imageJson.optString("type", null);
        String orientationString = imageJson.optString("orientation", "");
        if (orientationString.equals("")) {
            image.orientation = EmpImage.Orientation.UNKNOWN;
        }
        else if (orientationString.equals("PORTRAIT")) {
            image.orientation = EmpImage.Orientation.PORTRAIT;
        }
        else if (orientationString.equals("LANDSCAPE")) {
            image.orientation = EmpImage.Orientation.LANDSCAPE;
        }
        return image;
    }
}
