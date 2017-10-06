package com.ebs.android.exposure.models;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by Joao Coelho on 15/07/2017.
 */

public class EmpProgram extends EmpAsset {
    public String programId;
    public String channelId;
    public DateTime startDateTime;
    public DateTime endDateTime;

    @Override
    public String getId() {
        return programId + "@" + channelId;
    }

    public boolean liveNow() {
        if(this.startDateTime == null || this.endDateTime == null) {
            return false;
        }
        DateTime now = DateTime.now();
        Duration duration1 = new Duration(now, this.startDateTime);
        Duration duration2 = new Duration(this.endDateTime, now);
        return duration1.getMillis() < 0 && duration2.getMillis() < 0;
    }

    public boolean isFuture() {
        if(this.startDateTime == null || this.endDateTime == null) {
            return true;
        }
        DateTime now = DateTime.now();
        Duration duration = new Duration(now, this.startDateTime);
        return duration.getMillis() > 0;
    }

    public String getTime(DateRef dref) {
        DateTime refDt = null;
        if(dref == DateRef.START) {
            refDt = this.startDateTime;
        }
        else {
            refDt = this.endDateTime;
        }
        if(refDt == null) {
            return "N/A";
        }
        String minutesStr =  refDt.getMinuteOfHour() < 10 ? "0" + Integer.toString(refDt.getMinuteOfHour()) : Integer.toString(refDt.getMinuteOfHour());
        String hourStr =  refDt.getHourOfDay() < 10 ? "0" + Integer.toString(refDt.getHourOfDay()) : Integer.toString(refDt.getHourOfDay());
        return hourStr + ":" + minutesStr;
    }

    public String timeHumanRefernce(DateRef dref) {
        if(this.startDateTime == null || this.endDateTime == null) {
            return "N/A";
        }
        DateTime refDt = null;
        if(dref == DateRef.START) {
            refDt = this.startDateTime;
        }
        else {
            refDt = this.endDateTime;
        }
        DateTime now = DateTime.now();
        DateTime yesterday = now.minusMillis(now.getMillisOfDay());
        DateTime beforeYesterday = yesterday.minusDays(1);
        DateTime tomorrow = yesterday.plusDays(1);
        DateTime afterTomorrow = tomorrow.plusDays(1);

        Duration duration = new Duration(beforeYesterday, refDt);
        String dayStr = refDt.getDayOfMonth() < 10 ? "0" + Integer.toString(refDt.getDayOfMonth()) : Integer.toString(refDt.getDayOfMonth());
        String monthStr = refDt.getMonthOfYear() < 10 ? "0" + Integer.toString(refDt.getMonthOfYear()) : Integer.toString(refDt.getMonthOfYear());
        if(duration.getMillis() < 0) {
            return dayStr + "/" + monthStr;
        }
        else {
            duration = new Duration(yesterday, refDt);
            if(duration.getMillis() < 0) {
                return "Yesterday";
            }
            else {
                duration = new Duration(tomorrow, refDt);
                if(duration.getMillis() < 0) {
                    return "Today";
                }
                else {
                    duration = new Duration(afterTomorrow, refDt);
                    if(duration.getMillis() < 0) {
                        return "Tomorrow";
                    }
                    else {
                        return dayStr + "/" + monthStr;
                    }
                }
            }
        }
    }

    public enum DateRef {
        START, END
    }
}
