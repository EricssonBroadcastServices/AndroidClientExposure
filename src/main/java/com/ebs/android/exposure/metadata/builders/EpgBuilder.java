package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-19.
 */
public class EpgBuilder extends EmpBaseBuilder implements IExposureCallback {

    public EpgBuilder(IMetadataCallback<ArrayList<EmpProgram>> listener) {
        super(listener);
    }

    public ArrayList<EmpProgram> getMetadata(JSONObject payload) {
        ArrayList<EmpProgram> programs = new ArrayList<EmpProgram>();

        try {
            String channelId = payload.getString("channelId");
            JSONArray programsJson = payload.getJSONArray("programs");

            for(int i = 0; i < programsJson.length(); ++i) {
                try {
                    EmpProgram program = new EmpProgram();

                    JSONObject programJson = programsJson.getJSONObject(i);
                    JSONObject programAssetJson = programJson.getJSONObject("asset");

                    program = (EmpProgram) this.getAsset(programAssetJson, program, true);
                    if(program != null) {
                        this.getProgram(programJson, program);
                        program.channelId = channelId;
                        programs.add(program);
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

        return programs;
    }

    @Override
    public void onCallCompleted(JSONObject response, ExposureError error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
