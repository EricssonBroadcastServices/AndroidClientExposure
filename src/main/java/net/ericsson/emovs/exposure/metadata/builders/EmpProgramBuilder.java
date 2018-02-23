package net.ericsson.emovs.exposure.metadata.builders;

import net.ericsson.emovs.exposure.interfaces.IExposureCallback;
import net.ericsson.emovs.utilities.interfaces.IMetadataCallback;
import net.ericsson.emovs.utilities.errors.Error;
import net.ericsson.emovs.utilities.metadata.EmpBaseBuilder;
import net.ericsson.emovs.utilities.models.EmpProgram;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2018-01-23.
 */

public class EmpProgramBuilder extends EmpBaseBuilder implements IExposureCallback {
    public EmpProgramBuilder(IMetadataCallback<EmpProgram> listener) {
        super(listener);
    }

    public EmpProgram getMetadata(JSONObject programJson) {
        ArrayList<EmpProgram> programs = new ArrayList<EmpProgram>();

        try {
            EmpProgram program = new EmpProgram();
            JSONObject programAssetJson = programJson.getJSONObject("asset");
            program = (EmpProgram) this.getAsset(programAssetJson, program, true);
            if (program != null) {
                this.getProgram(programJson, program);
                program.channelId = programJson.optString("channelId");
                return program;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
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
