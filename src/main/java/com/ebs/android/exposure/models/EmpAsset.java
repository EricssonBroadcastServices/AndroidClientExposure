package com.ebs.android.exposure.models;

import com.ebs.android.exposure.interfaces.IPlayable;

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

    public String getId() {
        return assetId;
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
    }
}
