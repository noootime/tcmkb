package com.kiki;

import java.util.Objects;

public class FirstTitle {
    private String title;
    private String url;
    private String classification;

    public FirstTitle() {
    }

    public FirstTitle(String title, String url, String classification) {
        this.title = title;
        this.url = url;
        this.classification = classification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "FirstTitle{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", classification='" + classification + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FirstTitle) {
            FirstTitle ft = (FirstTitle) obj;
            return Objects.equals((title + url + classification), (ft.getTitle() + ft.getUrl() + ft.getClassification()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title + url + classification);
    }
}
