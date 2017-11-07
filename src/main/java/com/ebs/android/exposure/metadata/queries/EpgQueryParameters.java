package com.ebs.android.exposure.metadata.queries;

/**
 * Created by Joao Coelho on 2017-09-26.
 */

public class EpgQueryParameters extends BaseQueryParams {
    private static final long DEFAULT_TIME_FRAME = 1000 * 60 * 60 * 24 * 2; // 2 days
    public static final EpgQueryParameters DEFAULT = new EpgQueryParameters().setFutureTimeFrame(DEFAULT_TIME_FRAME).setPastTimeFrame(DEFAULT_TIME_FRAME);

    long pastTimeFrame;
    long futureTimeFrame;

    public EpgQueryParameters() {

    }

    public long getPastTimeFrame() {
        return pastTimeFrame;
    }

    public EpgQueryParameters setPastTimeFrame(long pastTimeFrame) {
        this.pastTimeFrame = pastTimeFrame;
        return this;
    }

    public long getFutureTimeFrame() {
        return futureTimeFrame;
    }

    public EpgQueryParameters setFutureTimeFrame(long futureTimeFrame) {
        this.futureTimeFrame = futureTimeFrame;
        return this;
    }
}
