package com.ebs.android.exposure.metadata.builders;

import com.ebs.android.exposure.clients.exposure.ExposureError;
import com.ebs.android.exposure.interfaces.IExposureCallback;
import com.ebs.android.exposure.metadata.IMetadataCallback;
import com.ebs.android.exposure.models.EmpEpisode;
import com.ebs.android.exposure.models.EmpSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-07-21.
 */
public class SeriesBuilder extends EmpBaseBuilder implements IExposureCallback {

    public SeriesBuilder(IMetadataCallback<ArrayList<EmpSeries>> listener) {
        super(listener);
    }

    public ArrayList<EmpSeries> getMetadata(JSONObject payload) {
        ArrayList<EmpSeries> allSeries = new ArrayList<EmpSeries>();

        try {
            JSONArray allSeriesJson = payload.getJSONArray("items");

            for(int i = 0; i < allSeriesJson.length(); ++i) {
                try {
                    JSONObject seriesJson = allSeriesJson.getJSONObject(i);
                    EmpSeries series = new EmpSeries();

                    ArrayList<EmpEpisode> episodes = new ArrayList<EmpEpisode>();
                    JSONArray episodesJson = seriesJson.optJSONArray("episodes");

                    if (episodesJson != null) {
                        for(int j = 0; j < episodesJson.length(); ++j) {
                            JSONObject episodeJson = episodesJson.getJSONObject(j);
                            EmpEpisode episode = new EmpEpisode();
                            episode = (EmpEpisode) this.getAsset(episodeJson, episode, true);
                            if(episode != null) {
                                episode.episodeNr = episodeJson.optString("episode", "");
                                episodes.add(episode);
                            }
                        }
                    }

                    series.episodes = episodes;
                    series.name = this.getLocalized(seriesJson, "en", "title");
                    series.logoUrl = this.getLocalizedImages(seriesJson, "en", "thumbnail");
                    series.seriesId = seriesJson.getString("seasonId");

                    if (episodesJson == null || series.name == null || series.name.equals("")) {
                        continue;
                    }

                    allSeries.add(series);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return allSeries;
    }

    @Override
    public void onCallCompleted(JSONObject response, ExposureError error) {
        if (handleError(error)) {
            return;
        }
        this.listener.onMetadata(getMetadata(response));
    }
}
