package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpAsset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-18.
 */
public class AutocompleteBuilder extends EmpBaseBuilder implements IExposureCallback {

    public AutocompleteBuilder(IMetadataCallback<ArrayList<EmpAsset>> listener) {
        super(listener);
    }

    public ArrayList<EmpAsset> getMetadata(JSONObject payload) {
        ArrayList<EmpAsset> assets = new ArrayList<EmpAsset>();
        try {
            JSONArray items = payload.getJSONArray("items");
            for (int i = 0; i < items.length(); ++i) {
                try {
                    EmpAsset asset = new EmpAsset();

                    JSONObject assetJson = items.getJSONObject(i);
                    String assetId = assetJson.getString("assetId");
                    String title = assetJson.getString("text");

                    asset.title = title;
                    asset.assetId = assetId;

                    assets.add(asset);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return assets;
    }

    @Override
    public void onCallCompleted(JSONObject response, ExposureError error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
