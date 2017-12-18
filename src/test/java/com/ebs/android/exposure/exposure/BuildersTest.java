package com.ebs.android.exposure.exposure;

import net.ericsson.emovs.exposure.auth.Credentials;
import net.ericsson.emovs.exposure.metadata.builders.CarouselGroupBuilder;
import net.ericsson.emovs.exposure.metadata.builders.EmpBaseBuilder;
import net.ericsson.emovs.exposure.metadata.builders.EpgBuilder;
import net.ericsson.emovs.exposure.metadata.builders.SeriesBuilder;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.models.EmpCarousel;
import net.ericsson.emovs.utilities.models.EmpProgram;
import net.ericsson.emovs.utilities.models.EmpSeries;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2017 Ericsson. All Rights Reserved
 *
 * This SOURCE CODE FILE, which has been provided by Ericsson as part
 * of an Ericsson software product for use ONLY by licensed users of the
 * product, includes CONFIDENTIAL and PROPRIETARY information of Ericsson.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF
 * THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 */

@RunWith(RobolectricTestRunner.class)
public class BuildersTest {

    @Test
    public void getAssetTest() throws Exception {
        String assetJsonString = "{\"created\":\"2017-09-20T09:05:04.897Z\",\"changed\":\"2017-10-10T11:46:10.415Z\",\"assetId\":\"d8525e2d-4f2a-4fe1-9ea8-adbb531dc678_enigma\",\"type\":\"MOVIE\",\"localized\":[{\"locale\":\"en\",\"title\":\"stump\",\"images\":[{\"url\":\"https:\\/\\/azukifilesprestage.blob.core.windows.net\\/img\\/6e59033f3ebf23ea305b5e74457982ac\\/6e59033f3ebf23ea305b5e74457982ac.jpg\",\"height\":360,\"width\":480,\"orientation\":\"LANDSCAPE\",\"type\":\"poster\"}]},{\"locale\":\"sv\",\"title\":\"stump\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-09-20T09:15:08Z\",\"fromDate\":\"2017-09-20T09:16:27Z\",\"toDate\":\"2021-09-20T09:04:38Z\",\"countries\":[],\"services\":[\"pigeon\",\"web\",\"bike\"],\"products\":[\"EnigmaFVOD__DevGroup__EnigmaTV_enigma\",\"EnigmaFVOD_enigma\"],\"publicationId\":\"d8525e2d-4f2a-4fe1-9ea8-adbb531dc678publ_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"popularityScores\":{\"4320\":\"0.2\",\"1440\":\"0.2\"},\"originalTitle\":\"2017 Specialized S-Works Stumpjumper FSR 6Fattie MTB test ride - The Singletrack Sampler\",\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"5f2aaf3d-9135-49e8-be3f-f677adffc129_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"durationMillis\":1089522,\"status\":\"enabled\"},{\"mediaId\":\"5f2aaf3d-9135-49e8-be3f-f677adffc129-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"durationMillis\":1089522,\"status\":\"enabled\"},{\"mediaId\":\"5f2aaf3d-9135-49e8-be3f-f677adffc129-fai-hls_enigma\",\"drm\":\"FAIRPLAY\",\"format\":\"HLS\",\"durationMillis\":1089522,\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[],\"userData\":{\"playHistory\":{\"lastViewedOffset\":313637,\"lastViewedTime\":1513243070775}}}";
        EmpAsset asset = new EmpAsset();
        EmpBaseBuilder builder = new EmpBaseBuilder(null);
        builder.getAsset(new JSONObject(assetJsonString), asset, true);
        assertEquals("d8525e2d-4f2a-4fe1-9ea8-adbb531dc678_enigma", asset.assetId);
        assertTrue(asset.durationMillis == 1089522);
    }

    @Test
    public void getProgramTest() throws Exception {
        String programJsonString = "{\"programId\":\"tv4se_20171216145500_enigma\",\"assetId\":\"tv4se_20171216145500_enigma\",\"channelId\":\"ch01_enigma\",\"startTime\":\"2017-12-16T14:55:00Z\",\"endTime\":\"2017-12-16T15:55:00Z\",\"vodAvailable\":false,\"catchup\":false,\"catchupBlocked\":false,\"asset\":{\"created\":\"2017-12-13T00:26:16.279Z\",\"assetId\":\"tv4se_20171216145500_enigma\",\"type\":\"LIVE_EVENT\",\"localized\":[{\"locale\":\"sv\",\"title\":\"Enkelstöten\",\"description\":\"Jenny och Cecilia blir bestulna på alla pengar men Stellan fortsätter att kräva sin andel vilket får Jenny att bryta ihop. Cecilia bestämmer sig för att försöka lösa situationen på egen hand. Jenny och Harriet hittar tillbaka till varandra vilket leder till att Jenny gör något hon ängrar. När Jenny blir varse om Cecilias planer ger de sig båda ut på sitt farligaste äventyr hittills. Samtidigt är polisen hack i häl på dem.\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-12-09T14:55:00Z\",\"fromDate\":\"2017-12-16T14:55:00Z\",\"toDate\":\"2017-12-23T15:55:00Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"tv4se_20171216145500_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[{\"personId\":\"Sissela Kyle\",\"name\":\"Sissela Kyle\",\"function\":\"actor\"},{\"personId\":\"Lotta Tejle\",\"name\":\"Lotta Tejle\",\"function\":\"actor\"},{\"personId\":\"Tomas von Brömssen\",\"name\":\"Tomas von Brömssen\",\"function\":\"actor\"},{\"personId\":\"Ralph Carlsson\",\"name\":\"Ralph Carlsson\",\"function\":\"actor\"},{\"personId\":\"Peter Carlberg\",\"name\":\"Peter Carlberg\",\"function\":\"actor\"},{\"personId\":\"Kristin Andersson\",\"name\":\"Kristin Andersson\",\"function\":\"actor\"}],\"productionYear\":2015,\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"tv4se_20171216145500-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"programId\":\"tv4se_20171216145500_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"tv4se_20171216145500-une-hls_enigma\",\"drm\":\"UNENCRYPTED\",\"format\":\"HLS\",\"programId\":\"tv4se_20171216145500_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"tv4se_20171216145500_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"programId\":\"tv4se_20171216145500_enigma\",\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[]},\"blackout\":false}";
        EmpProgram program = new EmpProgram();
        EmpBaseBuilder builder = new EmpBaseBuilder(null);
        builder.getProgram(new JSONObject(programJsonString), program);
        assertEquals("tv4se_20171216145500_enigma", program.programId);
        assertTrue(program.startDateTime != null);
        assertTrue(program.endDateTime != null);
    }

    @Test
    public void getSeriesTest() throws Exception {
        String str = "{\"totalCount\":5,\"pageSize\":50,\"pageNumber\":1,\"items\":[{\"changed\":\"2017-11-21T15:52:25.761Z\",\"season\":\"1\",\"tags\":[],\"localized\":[{\"locale\":\"en\",\"title\":\"Season 1\",\"images\":[]}],\"tvShowId\":\"Bloodline_enigma\",\"seasonId\":\"Bloodline_s1_enigma\",\"episodeCount\":9,\"episodes\":[{\"created\":\"2017-06-07T14:01:32.302Z\",\"changed\":\"2017-11-21T15:52:25.803Z\",\"assetId\":\"Bloodline_s1_e1_enigma\",\"type\":\"EPISODE\",\"localized\":[{\"locale\":\"en\",\"title\":\"Part 1\",\"images\":[{\"url\":\"https://azukifilesprestage.blob.core.windows.net/img/72ad1fc47587f59a6ec336e30720de6f/72ad1fc47587f59a6ec336e30720de6f.jpg\",\"height\":730,\"width\":1296,\"orientation\":\"LANDSCAPE\",\"type\":\"thumbnail\"}]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-03-07T14:22:28.264Z\",\"fromDate\":\"2017-03-14T13:22:28.263Z\",\"toDate\":\"2028-03-14T15:22:28.263Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"Bloodline_s1_e1_pub_enigma\",\"customData\":{},\"devices\":[]}],\"episode\":\"1\",\"season\":\"1\",\"seasonId\":\"Bloodline_s1_enigma\",\"participants\":[],\"originalTitle\":\"Part 1\",\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"6fd92bdf-14b1-4240-a950-3710d52db01d-3_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"durationMillis\":136345,\"status\":\"enabled\"},{\"mediaId\":\"6fd92bdf-14b1-4240-a950-3710d52db01d-3-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"durationMillis\":136345,\"status\":\"enabled\"},{\"mediaId\":\"6fd92bdf-14b1-4240-a950-3710d52db01d-3-une-hls_enigma\",\"drm\":\"UNENCRYPTED\",\"format\":\"HLS\",\"durationMillis\":136345,\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"runtime\":0,\"tvShowId\":\"Bloodline_enigma\",\"customData\":{},\"externalReferences\":[],\"markers\":[]}],\"linkedEntities\":[],\"externalReferences\":[],\"customData\":null}]}";
        SeriesBuilder builder = new SeriesBuilder(null);
        ArrayList<EmpSeries> series = builder.getMetadata(new JSONObject(str));
        assertTrue(series.size() == 1);
        assertTrue(series.get(0).episodes.size() == 1);
    }

    @Test
    public void getCarouselTest() throws Exception {
        String str = "{\"items\":[{\"carouselId\":\"basicCarousel1\",\"sortOrder\":10,\"items\":{\"totalCount\":369,\"pageSize\":20,\"pageNumber\":1,\"items\":[{\"created\":\"2017-12-18T09:05:34.596Z\",\"changed\":\"2017-12-18T09:13:29.497Z\",\"assetId\":\"4428cb6a-e83a-4a98-95d9-474463ee09de_enigma\",\"type\":\"MOVIE\",\"localized\":[{\"locale\":\"en\",\"title\":\"Elena Malakhatka - VR, The Hero's Journey\",\"sortingTitle\":\"VR, The Hero's Journey\",\"images\":[{\"url\":\"https://azukifilesprestage.blob.core.windows.net/img/b0c58249a7a959df8b6dbb2aa4d9523e/b0c58249a7a959df8b6dbb2aa4d9523e.png\",\"height\":1080,\"width\":1920,\"orientation\":\"LANDSCAPE\",\"type\":\"banner\"}]}],\"tags\":[{\"type\":\"other\",\"tagValues\":[{\"tagId\":\"5e992616-70b1-4598-8b42-8b9c72a0ec66_devgroup\"}]}],\"publications\":[{\"publicationDate\":\"2017-12-18T08:59:51Z\",\"fromDate\":\"2017-12-18T08:59:51Z\",\"toDate\":\"2018-12-25T08:59:51Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"4428cb6a-e83a-4a98-95d9-474463ee09depubl_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"productionYear\":2017,\"popularityScores\":{\"180\":\"0.5\",\"1440\":\"0.2\",\"4320\":\"0.2\"},\"originalTitle\":\"Elena Malakhatka - VR, The Hero's Journey\",\"live\":false,\"productionCountries\":[\"SE\"],\"subtitles\":[],\"audioTracks\":[\"en\"],\"defaultAudioTrack\":\"en\",\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"4189e1fc-d1b2-4c70-bd69-1b8ca93348a5_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"durationMillis\":1113800,\"status\":\"enabled\"},{\"mediaId\":\"4189e1fc-d1b2-4c70-bd69-1b8ca93348a5-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"durationMillis\":1113800,\"status\":\"enabled\"},{\"mediaId\":\"4189e1fc-d1b2-4c70-bd69-1b8ca93348a5-fai-hls_enigma\",\"drm\":\"FAIRPLAY\",\"format\":\"HLS\",\"durationMillis\":1113800,\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"runtime\":1113,\"customData\":{},\"externalReferences\":[],\"markers\":[]},{\"created\":\"2017-12-17T13:46:43.37Z\",\"changed\":\"2017-12-18T07:50:06.387Z\",\"assetId\":\"2a5a485e-fd57-4a8a-8c59-acee4b582280_enigma\",\"type\":\"MOVIE\",\"localized\":[{\"locale\":\"en\",\"title\":\"Die Hard\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-12-17T13:37:57Z\",\"fromDate\":\"2017-12-17T13:37:57Z\",\"toDate\":\"2017-12-24T13:37:57Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"2a5a485e-fd57-4a8a-8c59-acee4b582280publ_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[]}]},\"titles\":[{\"locale\":\"en\",\"title\":\"New\"}]}]}";
        CarouselGroupBuilder builder = new CarouselGroupBuilder(null);
        ArrayList<EmpCarousel> carousel = builder.getMetadata(new JSONObject(str));
        assertTrue(carousel.size() == 1);
        assertTrue(carousel.get(0).assets.size() == 1);
    }

    @Test
    public void getEpgTest() throws Exception {
        String str = "{\"channelId\":\"ch03_enigma\",\"programs\":[{\"programId\":\"sportcmorese_20171216141000_enigma\",\"assetId\":\"sportcmorese_20171216141000_enigma\",\"channelId\":\"ch03_enigma\",\"startTime\":\"2017-12-16T14:10:00Z\",\"endTime\":\"2017-12-16T16:50:00Z\",\"vodAvailable\":false,\"catchup\":false,\"catchupBlocked\":false,\"asset\":{\"created\":\"2017-12-13T00:26:08.612Z\",\"assetId\":\"sportcmorese_20171216141000_enigma\",\"type\":\"LIVE_EVENT\",\"localized\":[{\"locale\":\"sv\",\"title\":\"HockeyAllsvenskan\",\"description\":\"Omgång 27. Direktsändning.\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-12-09T14:10:00Z\",\"fromDate\":\"2017-12-16T14:10:00Z\",\"toDate\":\"2017-12-23T16:50:00Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"sportcmorese_20171216141000_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"productionYear\":2017,\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"sportcmorese_20171216141000-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"programId\":\"sportcmorese_20171216141000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216141000-fai-hls_enigma\",\"drm\":\"FAIRPLAY\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216141000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216141000_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216141000_enigma\",\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[]},\"blackout\":false},{\"programId\":\"sportcmorese_20171216165000_enigma\",\"assetId\":\"sportcmorese_20171216165000_enigma\",\"channelId\":\"ch03_enigma\",\"startTime\":\"2017-12-16T16:50:00Z\",\"endTime\":\"2017-12-16T19:30:00Z\",\"vodAvailable\":false,\"catchup\":false,\"catchupBlocked\":false,\"asset\":{\"created\":\"2017-12-13T00:26:08.612Z\",\"assetId\":\"sportcmorese_20171216165000_enigma\",\"type\":\"LIVE_EVENT\",\"localized\":[{\"locale\":\"sv\",\"title\":\"HockeyAllsvenskan\",\"description\":\"Omgång 27. Direktsändning.\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-12-09T16:50:00Z\",\"fromDate\":\"2017-12-16T16:50:00Z\",\"toDate\":\"2017-12-23T19:30:00Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"sportcmorese_20171216165000_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"productionYear\":2017,\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"sportcmorese_20171216165000-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"programId\":\"sportcmorese_20171216165000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216165000-fai-hls_enigma\",\"drm\":\"FAIRPLAY\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216165000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216165000_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216165000_enigma\",\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[]},\"blackout\":false},{\"programId\":\"sportcmorese_20171216193000_enigma\",\"assetId\":\"sportcmorese_20171216193000_enigma\",\"channelId\":\"ch03_enigma\",\"startTime\":\"2017-12-16T19:30:00Z\",\"endTime\":\"2017-12-16T21:30:00Z\",\"vodAvailable\":false,\"catchup\":false,\"catchupBlocked\":false,\"asset\":{\"created\":\"2017-12-13T00:26:08.612Z\",\"assetId\":\"sportcmorese_20171216193000_enigma\",\"type\":\"LIVE_EVENT\",\"localized\":[{\"locale\":\"sv\",\"title\":\"Ishockey: Channel One Cup\",\"description\":\"Repris av matchen från 16/12.\",\"images\":[]}],\"tags\":[],\"publications\":[{\"publicationDate\":\"2017-12-09T19:30:00Z\",\"fromDate\":\"2017-12-16T19:30:00Z\",\"toDate\":\"2017-12-23T21:30:00Z\",\"countries\":[],\"services\":[],\"products\":[\"EnigmaFVOD_enigma\"],\"publicationId\":\"sportcmorese_20171216193000_enigma\",\"customData\":{},\"devices\":[]}],\"participants\":[],\"productionYear\":2017,\"live\":false,\"productionCountries\":[],\"subtitles\":[],\"audioTracks\":[],\"spokenLanguages\":[],\"medias\":[{\"mediaId\":\"sportcmorese_20171216193000-cen-das_enigma\",\"drm\":\"CENC\",\"format\":\"DASH\",\"programId\":\"sportcmorese_20171216193000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216193000-fai-hls_enigma\",\"drm\":\"FAIRPLAY\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216193000_enigma\",\"status\":\"enabled\"},{\"mediaId\":\"sportcmorese_20171216193000_enigma\",\"drm\":\"EDRM\",\"format\":\"HLS\",\"programId\":\"sportcmorese_20171216193000_enigma\",\"status\":\"enabled\"}],\"parentalRatings\":[],\"linkedEntities\":[],\"customData\":{},\"externalReferences\":[]},\"blackout\":false}],\"totalHitsAllChannels\":40}";
        EpgBuilder builder = new EpgBuilder(null);
        ArrayList<EmpProgram> epg = builder.getMetadata(new JSONObject(str));
        assertTrue(epg.size() == 3);
    }

}