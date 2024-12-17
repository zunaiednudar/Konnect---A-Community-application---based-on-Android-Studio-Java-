package com.example.konnect_v2;

public class NewsArticleListModel {
    private String title, content, canonicalLink, uriImageLink, publishOn;

    public NewsArticleListModel() {
    }

    public NewsArticleListModel(String title, String content, String canonicalLink, String uriImageLink, String publishOn) {
        this.title = title;
        this.content = content;
        this.canonicalLink = canonicalLink;
        this.uriImageLink = uriImageLink;
        this.publishOn = publishOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCanonicalLink() {
        return canonicalLink;
    }

    public void setCanonicalLink(String canonicalLink) {
        this.canonicalLink = canonicalLink;
    }

    public String getUriImageLink() {
        return uriImageLink;
    }

    public void setUriImageLink(String uriImageLink) {
        this.uriImageLink = uriImageLink;
    }

    public String getPublishOn() {
        return publishOn;
    }

    public void setPublishOn(String publishOn) {
        this.publishOn = publishOn;
    }
}
