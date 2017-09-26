package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joao Coelho on 2017-07-18.
 */
public class MainConfigBuilder extends EmpBaseBuilder implements IExposureCallback {

    public MainConfigBuilder(IMetadataCallback<EmpCustomer> listener) {
        super(listener);
    }

    private EmpCustomer getMetadata(JSONObject payload) {
        EmpCustomer customer = new EmpCustomer();
        customer.withCarouselGroupId(getCarouselGroupId(payload));
        return customer;
    }

    private String getCarouselGroupId(JSONObject mainConfig) {
        if (mainConfig == null) {
            return null;
        }

        if (mainConfig.has("config") == false) {
            return null;
        }

        JSONArray configs;
        try {
            configs = mainConfig.getJSONArray("config");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (configs.length() == 0) {
            return null;
        }

        try {
            JSONObject config = configs.getJSONObject(0);
            String carouselGroupId = config.getString("carouselGroupId");
            return carouselGroupId;
        } catch (JSONException e) {
        }

        return null;
    }

    @Override
    public void onCallCompleted(JSONObject response, ExposureError error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
