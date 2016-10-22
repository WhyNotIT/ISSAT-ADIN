package com.whynotit.admin.Models;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Harzallah on 20/09/2016.
 */
public class Comment {
    String comment;
    long timestamp;
    String idWriter;
    String idNews;
    private String key;

    public Comment() {
    }

    public Comment(String key, String comment, long timestamp, String idWriter, String idNews) {
        this.key = key;
        this.comment = comment;
        this.timestamp = timestamp;
        this.idWriter = idWriter;
        this.idNews = idNews;
    }

    public String getKey() {
        return key;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdWriter() {
        return idWriter;
    }

    public void setIdWriter(String idWriter) {
        this.idWriter = idWriter;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    public String getDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);

        return (calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.YEAR));
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                ", idWriter='" + idWriter + '\'' +
                ", idNews='" + idNews + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
