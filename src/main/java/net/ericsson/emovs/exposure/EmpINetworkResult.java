package net.ericsson.emovs.exposure;

/**
 * Created by Joao Coelho on 16/07/2017.
 */

public interface EmpINetworkResult {
    public void ok(String response);
    public void error(String error);
}
