package com.ebs.android.exposure.models;

import com.ebs.android.exposure.interfaces.IPlayable;

import java.io.Serializable;

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
}
