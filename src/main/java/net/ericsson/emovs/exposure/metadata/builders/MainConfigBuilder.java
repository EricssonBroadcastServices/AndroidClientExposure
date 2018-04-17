package net.ericsson.emovs.exposure.metadata.builders;

import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.interfaces.IMetadataCallback;
import net.ericsson.emovs.utilities.metadata.EmpBaseBuilder;
import net.ericsson.emovs.utilities.models.EmpCustomer;
import net.ericsson.emovs.utilities.errors.Error;

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
        return getCustomerDetails(payload);
    }

    private EmpCustomer getCustomerDetails(JSONObject mainConfig) {
        if (mainConfig == null) {
            return null;
        }

        if (mainConfig.has("config") == false) {
            return null;
        }

        try {
            EmpCustomer customer = new EmpCustomer();

            JSONObject config = mainConfig.getJSONObject("config");
            customer.withCarouselGroupId(config.getString("carouselGroupId"))
                    .withLogoUrl(config.getString("logoUrl"))
                    .withServiceName(config.getString("serviceName"));


            return customer;
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
