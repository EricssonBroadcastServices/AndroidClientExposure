package net.ericsson.emovs.exposure.models;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joao Coelho on 2017-09-07.
 */

public class EmpOfflineAsset extends EmpAsset {
    public String localMediaPath;

    @Override
    public JSONObject getJson() {
        JSONObject asset = super.getJson();
        try {
            asset.put("localSrc", localMediaPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return asset;
    }

    @Override
    public String getId() {
        return assetId;
    }
}
