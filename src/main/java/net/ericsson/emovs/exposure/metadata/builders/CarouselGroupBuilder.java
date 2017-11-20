package net.ericsson.emovs.exposure.metadata.builders;


import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.exposure.metadata.IMetadataCallback;
import net.ericsson.emovs.exposure.models.EmpAsset;
import net.ericsson.emovs.exposure.models.EmpCarousel;
import net.ericsson.emovs.utilities.Error;

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

                carousel.assets = new ArrayList<>();
                fillLocalized(carouselJson, carousel.localized);

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
    public void onCallCompleted(JSONObject response, Error error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
