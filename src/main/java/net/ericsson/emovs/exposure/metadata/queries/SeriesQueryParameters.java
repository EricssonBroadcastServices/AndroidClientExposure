package net.ericsson.emovs.exposure.metadata.queries;

/**
 * Created by Joao Coelho on 2017-09-26.
 */

public class SeriesQueryParameters extends BaseQueryParams {
    boolean includeEpisodes;

    public SeriesQueryParameters() {

    }

    public boolean includeEpisodes() {
        return includeEpisodes;
    }

    public SeriesQueryParameters withIncludeEpisodes(boolean value) {
        this.includeEpisodes = value;
        return this;
    }

    public static SeriesQueryParameters getDefault() {
        return new SeriesQueryParameters().withIncludeEpisodes(true);
    }
}
