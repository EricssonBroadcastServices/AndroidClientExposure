package net.ericsson.emovs.exposure.metadata.builders;

import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.exposure.metadata.IMetadataCallback;
import net.ericsson.emovs.exposure.models.EmpCustomer;
import net.ericsson.emovs.utilities.Error;

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

        try {
            JSONObject config = mainConfig.getJSONObject("config");
            String carouselGroupId = config.getString("carouselGroupId");
            return carouselGroupId;
        }
        catch (JSONException e) {
        }

        return null;
    }

    @Override
    public void onCallCompleted(JSONObject response, Error error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
