package com.ebs.android.exposure.models;

import com.ebs.android.exposure.interfaces.IPlayable;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 15/07/2017.
 */

public class EmpChannel extends IPlayable {
    public String channelId;
    public String name;
    public ArrayList<EmpProgram> programs;
    public String logoUrl;

    public EmpProgram liveProgram() {
        if(programs == null || programs.size() == 0) {
            return null;
        }
        for(int i = 0; i < this.programs.size(); ++i) {
            if (programs.get(i).liveNow()) {
                return programs.get(i);
            }
        }
        return null;
    }

    public int liveProgramIndex() {
        if(programs == null || programs.size() == 0) {
            return 0;
        }
        for(int i = 0; i < this.programs.size(); ++i) {
            if (programs.get(i).liveNow()) {
                return i;
            }
        }
        return 0;
    }

    public String getId() {
        return "live@" + channelId;
    }
}
