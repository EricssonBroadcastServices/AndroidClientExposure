package com.ebs.android.exposure.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Joao Coelho on 2017-11-07.
 */

public class LocalizedMetadata implements Serializable {
    public HashMap<String, String> titles;
    public HashMap<String, String> descriptions;
    public HashMap<String, ArrayList<EmpImage>> images;

    public LocalizedMetadata() {
        titles = new HashMap<>();
        images = new HashMap<>();
        descriptions = new HashMap<>();
    }

    public String getDescriptions(String locale) {
        if (this.descriptions.containsKey(locale) == false) {
            return null;
        }
        return this.descriptions.get(locale);
    }

    public String getTitle(String locale) {
        if (this.titles.containsKey(locale) == false) {
            return null;
        }
        return this.titles.get(locale);
    }

    public EmpImage getImage(String locale, String filterType) {
        if (this.images.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.images.get(locale)) {
            if (image.type.equals(filterType)) {
                return image;
            }
        }
        return null;
    }

    public EmpImage getImage(String locale, EmpImage.Orientation filterOrientation) {
        if (this.images.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.images.get(locale)) {
            if (image.orientation == filterOrientation) {
                return image;
            }
        }
        return null;
    }

    public EmpImage getImage(String locale, EmpImage.Orientation filterOrientation, String filterType) {
        if (this.images.containsKey(locale) == false) {
            return null;
        }
        for (EmpImage image : this.images.get(locale)) {
            if (image.orientation == filterOrientation && filterType.equals(image.type)) {
                return image;
            }
        }
        return null;
    }
}
