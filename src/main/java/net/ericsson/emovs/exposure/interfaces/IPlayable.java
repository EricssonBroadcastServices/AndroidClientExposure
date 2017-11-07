package net.ericsson.emovs.exposure.interfaces;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Joao Coelho on 16/07/2017.
 */

public abstract class IPlayable implements Serializable {
    public abstract String getId();
    public abstract JSONObject getJson();
}
