package net.ericsson.emovs.exposure.metadata.builders;



import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.interfaces.IMetadataCallback;
import net.ericsson.emovs.utilities.metadata.EmpBaseBuilder;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.errors.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Joao Coelho on 23/07/2017.
 */

public class AssetListBuilder extends EmpBaseBuilder implements IExposureCallback {

    public AssetListBuilder(IMetadataCallback<ArrayList<EmpAsset>> listener) {
        super(listener);
    }

    public ArrayList<EmpAsset> getMetadata(JSONObject payload) {
        ArrayList<EmpAsset> assets = new ArrayList<EmpAsset>();

        try {
            JSONArray allAssetsJson = payload.getJSONArray("items");

            for(int i = 0; i < allAssetsJson.length(); ++i) {
                try {
                    JSONObject assetJson = allAssetsJson.getJSONObject(i);
                    EmpAsset asset = new EmpAsset();
                    asset = getAsset(assetJson, asset, true);
                    if(asset != null) {
                        assets.add(asset);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (JSONException e) {
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
