package com.ebs.android.exposure.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Joao Coelho on 15/07/2017.
 */

public class EmpSeries implements Serializable {
    public String seriesId;
    public String name;
    public ArrayList<EmpEpisode> episodes;
    public String logoUrl;

}
