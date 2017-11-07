package com.ebs.android.exposure.models;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-17.
 */

public class EmpCarousel {
    public ArrayList<EmpAsset> assets;
    public LocalizedMetadata localized;

    public EmpCarousel() {
        this.localized = new LocalizedMetadata();
    }
}
