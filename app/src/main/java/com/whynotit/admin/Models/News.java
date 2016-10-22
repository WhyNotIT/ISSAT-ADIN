package com.whynotit.admin.Models;

import java.util.List;

/**
 * Created by Harzallah on 18/09/2016.
 */
public class News {



    private String titre;
    private String contenu;
    private String url;
    private long timestamp;
    private String date;
    private int nbLike;
    private int nbComment;
    private int nbDislike;
    private String key;
    private List<Comment> newsComments;
    private boolean isListnerAddedForComments = false;

    public boolean isListnerAddedForComments() {
        return isListnerAddedForComments;
    }

    public void setListnerAddedForComments(boolean listnerAddedForComments) {
        isListnerAddedForComments = listnerAddedForComments;
    }

    public List<Comment> getNewsComments() {
        return newsComments;
    }

    public void setNewsComments(List<Comment> newsComments) {
        this.newsComments = newsComments;
    }

    public int getNbLike() {
        return nbLike;
    }

    public void setNbLike(int nbLike) {
        this.nbLike = nbLike;
    }

    public int getNbComment() {
        return nbComment;
    }

    public void setNbComment(int nbComment) {
        this.nbComment = nbComment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public News() {
    }

    public News(String titre, String contenu, String url) {
        this.titre = titre;
        this.contenu = contenu;
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getNbDislike() {
        return nbDislike;
    }

    public void setNbDislike(int nbDislike) {
        this.nbDislike = nbDislike;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "News{" +
                "titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + timestamp +
                ", date='" + date + '\'' +
                ", nbLike=" + nbLike +
                ", nbComment=" + nbComment +
                ", nbDislike=" + nbDislike +
                ", key='" + key + '\'' +
                '}';
    }
}
