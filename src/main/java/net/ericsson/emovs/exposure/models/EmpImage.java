package net.ericsson.emovs.exposure.models;

import java.io.Serializable;

/**
 * Created by Joao Coelho on 2017-11-06.
 */

public class EmpImage implements Serializable {
    public enum Orientation {
        UNKNOWN,
        LANDSCAPE,
        PORTRAIT,
    };
    public int width;
    public int height;
    public String url;
    public Orientation orientation;
    public String type;
}
