package net.ericsson.emovs.exposure.interfaces;

import net.ericsson.emovs.utilities.errors.Error;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public interface IExposureHeaderCallback extends IExposureCallback {
  void onCallCompleted(JSONObject response, Error error, Map<String,List<String>> headerFields);
}
