package com.kiki;

import java.util.Objects;

public class ThirdTitle {
    private String parentTitle;
    private String title;
    private String url;

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
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

    @Override
    public String toString() {
        return "ThirdTitle{" +
                "parentTitle='" + parentTitle + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThirdTitle) {
            ThirdTitle tt = (ThirdTitle) obj;
            return Objects.equals((parentTitle + title + url), (tt.getParentTitle() + tt.getTitle() + tt.getUrl()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTitle + title + url);
    }
}
