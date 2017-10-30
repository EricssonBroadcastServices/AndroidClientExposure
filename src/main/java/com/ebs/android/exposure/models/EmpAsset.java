package com.ebs.android.exposure.models;

import com.ebs.android.exposure.interfaces.IPlayable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joao Coelho on 15/07/2017.
 */
public class EmpAsset extends IPlayable {
    public String assetId;
    public String imageUrl;
    public String title;
    public String resolution;
    public String popularity;
    public String duration;
    private String jsonObj;

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

    public EmpAsset() {

    }

    public void setProps(EmpAsset other) {
        this.assetId = other.assetId;
        this.imageUrl = other.imageUrl;
        this.title = other.title;
        this.resolution = other.resolution;
        this.popularity = other.popularity;
        this.duration = other.duration;
        this.jsonObj = other.jsonObj;
    }
}
