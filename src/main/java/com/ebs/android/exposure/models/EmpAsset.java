package com.ebs.android.exposure.models;

import com.ebs.android.exposure.interfaces.IPlayable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Joao Coelho on 15/07/2017.
 */
public class EmpAsset extends IPlayable {
    public String assetId;
//    public String imageUrl;
    public String originalTitle;
    public String resolution;
    public String popularity;
    public String duration;
    private String jsonObj;

    public Long lastViewedOffset;
    public Long lastViewedTime;
    public HashMap<String, String> localizedTitles;
    public HashMap<String, ArrayList<EmpImage>> localizedImages;

    public EmpAsset() {
        localizedTitles = new HashMap<>();
        localizedImages = new HashMap<>();
    }

    public String getId() {
        return assetId;
    }

    @Override
    public JSONObject getJson() {
        try {
            return new JSONObject(this.jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setJson(JSONObject ob) {
        this.jsonObj = ob.toString();
    }

    public void setProps(EmpAsset other) {
        this.assetId = other.assetId;
        this.originalTitle = other.originalTitle;
        this.resolution = other.resolution;
        this.popularity = other.popularity;
        this.duration = other.duration;
        this.localizedImages.clear();
        this.localizedImages.putAll(other.localizedImages);
        this.localizedTitles.clear();
        this.localizedTitles.putAll(other.localizedTitles);
        this.jsonObj = other.jsonObj;
    }

    public String getTitle(String locale) {
        if (this.localizedTitles.containsKey(locale) == false) {
            return null;
        }
        return this.localizedTitles.get(locale);
    }

    public EmpImage getImage(String locale, String filterType) {
        if (this.localizedImages.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.localizedImages.get(locale)) {
            if (image.type.equals(filterType)) {
                return image;
            }
        }
        return null;
    }

    public EmpImage getImage(String locale, EmpImage.Orientation filterOrientation) {
        if (this.localizedImages.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.localizedImages.get(locale)) {
            if (image.orientation == filterOrientation) {
                return image;
            }
        }
        return null;
    }

    public EmpImage getImage(String locale, EmpImage.Orientation filterOrientation, String filterType) {
        if (this.localizedImages.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.localizedImages.get(locale)) {
            if (image.orientation == filterOrientation && filterType.equals(image.type)) {
                return image;
            }
        }
        return null;
    }
}
