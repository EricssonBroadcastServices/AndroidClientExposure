package net.ericsson.emovs.exposure.metadata.builders;

import net.ericsson.emovs.exposure.metadata.IMetadataCallback;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.models.EmpImage;
import net.ericsson.emovs.utilities.models.EmpProgram;
import net.ericsson.emovs.utilities.models.LocalizedMetadata;
import net.ericsson.emovs.utilities.errors.Error;

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

    protected boolean handleError(Error error) {
        if (listener == null) {
            return true;
        }

        if (error != null) {
            this.listener.onError(error);
            return true;
        }

        return false;
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

    public EmpProgram getProgram(JSONObject programJson, EmpProgram program) throws JSONException {
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
