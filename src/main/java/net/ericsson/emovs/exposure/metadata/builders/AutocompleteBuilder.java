package net.ericsson.emovs.exposure.metadata.builders;



import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.exposure.metadata.IMetadataCallback;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.errors.Error;

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

                    asset.originalTitle = title;
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
    public void onCallCompleted(JSONObject response, Error error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
