package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpAsset;
import com.ebs.android.exposure.models.EmpCarousel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-17.
 */

public class CarouselGroupBuilder extends EmpBaseBuilder implements IExposureCallback {

    public CarouselGroupBuilder(IMetadataCallback<ArrayList<EmpCarousel>> listener) {
        super(listener);
    }

    public ArrayList<EmpCarousel> getMetadata(JSONObject payload) {
        ArrayList<EmpCarousel> carousels = new ArrayList<>();
        JSONArray carouselsJson;
        try {
            carouselsJson = payload.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
            return carousels;
        }

        for (int i = 0; i < carouselsJson.length(); ++i) {
            try {
                JSONObject carouselJson = carouselsJson.getJSONObject(i);
                JSONArray carouselAssets = carouselJson.getJSONObject("items").getJSONArray("items");
                EmpCarousel carousel = new EmpCarousel();

                String carouselTitle = "";
                if (carouselJson.has ("titles")) {
                    JSONArray localized = carouselJson.getJSONArray("titles");
                    carouselTitle = getLocalized(localized, "en", "title");
                }

                carousel.name = carouselTitle;
                carousel.assets = new ArrayList<>();

                for (int j = 0; j < carouselAssets.length(); ++j) {
                    JSONObject assetJson = carouselAssets.getJSONObject(j);
                    EmpAsset asset = new EmpAsset();
                    asset = getAsset(assetJson, asset, true);
                    if(asset != null) {
                        carousel.assets.add(asset);
                    }
                }

                carousels.add(carousel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return carousels;
    }

    @Override
    public void onCallCompleted(JSONObject response, ExposureError error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
