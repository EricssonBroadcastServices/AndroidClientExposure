package net.ericsson.emovs.exposure.metadata.builders;


import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.exposure.metadata.IMetadataCallback;
import net.ericsson.emovs.utilities.models.EmpChannel;
import net.ericsson.emovs.utilities.errors.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-18.
 */
public class ChannelsBuilder extends EmpBaseBuilder implements IExposureCallback {

    public ChannelsBuilder(IMetadataCallback<ArrayList<EmpChannel>> listener) {
        super(listener);
    }

    public ArrayList<EmpChannel> getMetadata(JSONObject payload) {
        ArrayList<EmpChannel> channels = new ArrayList<EmpChannel>();

        try {
            JSONArray items = payload.getJSONArray("items");

            for(int i = 0; i < items.length(); ++i) {
                try {
                    EmpChannel channel = new EmpChannel();
                    JSONObject channelJson = items.getJSONObject(i);
                    String channelId = channelJson.getString("assetId");
                    channel.originalTitle = channelJson.optString("originalTitle", null);
                    channel.channelId = channelId;
                    fillLocalized(channelJson, channel.localized);
                    channels.add(channel);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return channels;
    }


    @Override
    public void onCallCompleted(JSONObject response, Error error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }

}
